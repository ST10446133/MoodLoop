const { onCall, HttpsError } = require("firebase-functions/v2/https");
const admin = require("firebase-admin");

admin.initializeApp();

const db = admin.firestore();
const FieldValue = admin.firestore.FieldValue;

const MOOD_POINTS = 10;
const REFLECTION_POINTS = 15;

exports.logMood = onCall(async (request) => {
  const uid = requireUser(request);
  const emotion = cleanRequiredString(request.data.emotion, "emotion", 30);
  const note = cleanOptionalString(request.data.note, 300);
  const moodRef = db.collection("users").doc(uid).collection("moods").doc();
  const now = Date.now();

  await db.runTransaction(async (transaction) => {
    const userRef = db.collection("users").doc(uid);
    const userSnap = await transaction.get(userRef);
    const stats = nextStats(userSnap.data(), MOOD_POINTS, true);

    transaction.set(moodRef, {
      id: moodRef.id,
      emotion,
      note,
      createdAtMillis: now,
      createdAt: FieldValue.serverTimestamp(),
      pointsAwarded: MOOD_POINTS,
    });

    transaction.set(userRef, {
      points: stats.points,
      level: stats.level,
      currentStreak: stats.currentStreak,
      bestStreak: stats.bestStreak,
      lastActivityDate: stats.lastActivityDate,
      moodCount: FieldValue.increment(1),
      updatedAt: FieldValue.serverTimestamp(),
    }, { merge: true });

    addAchievement(transaction, userRef, "First mood", now);
    addLevelAchievement(transaction, userRef, stats.level, now);
  });

  return {
    ok: true,
    moodId: moodRef.id,
    pointsAwarded: MOOD_POINTS,
  };
});

exports.saveReflection = onCall(async (request) => {
  const uid = requireUser(request);
  const text = cleanRequiredString(request.data.text, "reflection", 1000);
  const emotion = cleanRequiredString(request.data.emotion, "emotion", 30);
  const shareAnonymously = request.data.shareAnonymously === true;
  const reflectionRef = db.collection("users").doc(uid).collection("reflections").doc();
  const now = Date.now();

  await db.runTransaction(async (transaction) => {
    const userRef = db.collection("users").doc(uid);
    const userSnap = await transaction.get(userRef);
    const stats = nextStats(userSnap.data(), REFLECTION_POINTS, false);
    const sharing = shareAnonymously ? "Shared anonymously" : "Private";

    const reflection = {
      id: reflectionRef.id,
      text,
      emotion,
      sharing,
      sharedAnonymously: shareAnonymously,
      createdAtMillis: now,
      createdAt: FieldValue.serverTimestamp(),
      helped: 0,
      pointsAwarded: REFLECTION_POINTS,
    };

    transaction.set(reflectionRef, reflection);
    transaction.set(userRef, {
      points: stats.points,
      level: stats.level,
      currentStreak: stats.currentStreak,
      bestStreak: stats.bestStreak,
      lastActivityDate: stats.lastActivityDate,
      reflectionCount: FieldValue.increment(1),
      updatedAt: FieldValue.serverTimestamp(),
    }, { merge: true });

    addAchievement(transaction, userRef, "First reflection", now);
    addLevelAchievement(transaction, userRef, stats.level, now);

    if (shareAnonymously) {
      addAchievement(transaction, userRef, "Shared support", now);
      transaction.set(db.collection("wallPosts").doc(reflectionRef.id), {
        id: reflectionRef.id,
        reflectionId: reflectionRef.id,
        ownerId: uid,
        emotion,
        text,
        createdAtMillis: now,
        createdAt: FieldValue.serverTimestamp(),
        helped: 0,
        anonymous: true,
      });
    }
  });

  return {
    ok: true,
    reflectionId: reflectionRef.id,
    pointsAwarded: REFLECTION_POINTS,
    sharedAnonymously,
  };
});

exports.markWallPostHelpful = onCall(async (request) => {
  const uid = requireUser(request);
  const postId = cleanRequiredString(request.data.postId, "postId", 120);
  const userHelpedRef = db.collection("users").doc(uid).collection("helpedPosts").doc(postId);
  const wallPostRef = db.collection("wallPosts").doc(postId);

  const result = await db.runTransaction(async (transaction) => {
    const existingClick = await transaction.get(userHelpedRef);
    if (existingClick.exists) {
      return { alreadyMarked: true };
    }

    transaction.set(userHelpedRef, {
      postId,
      clickedAt: FieldValue.serverTimestamp(),
    });
    transaction.set(wallPostRef, {
      helped: FieldValue.increment(1),
      updatedAt: FieldValue.serverTimestamp(),
    }, { merge: true });
    return { alreadyMarked: false };
  });

  return {
    ok: true,
    alreadyMarked: result.alreadyMarked,
  };
});

function requireUser(request) {
  if (!request.auth || !request.auth.uid) {
    throw new HttpsError("unauthenticated", "Please sign in first.");
  }
  return request.auth.uid;
}

function cleanRequiredString(value, fieldName, maxLength) {
  const text = cleanOptionalString(value, maxLength);
  if (!text) {
    throw new HttpsError("invalid-argument", `${fieldName} is required.`);
  }
  return text;
}

function cleanOptionalString(value, maxLength) {
  if (typeof value !== "string") return "";
  return value.trim().slice(0, maxLength);
}

function nextStats(userData, pointsToAdd, updatesStreak) {
  const currentPoints = numberValue(userData && userData.points);
  const points = currentPoints + pointsToAdd;
  const level = Math.floor(points / 100) + 1;
  const today = todayKey();
  const yesterday = offsetDayKey(-1);
  const lastActivityDate = userData && userData.lastActivityDate;
  const currentStreak = numberValue(userData && userData.currentStreak);
  const bestStreak = numberValue(userData && userData.bestStreak);

  if (!updatesStreak || lastActivityDate === today) {
    return {
      points,
      level,
      currentStreak,
      bestStreak,
      lastActivityDate: lastActivityDate || today,
    };
  }

  const nextCurrentStreak = lastActivityDate === yesterday ? currentStreak + 1 : 1;
  return {
    points,
    level,
    currentStreak: nextCurrentStreak,
    bestStreak: Math.max(bestStreak, nextCurrentStreak),
    lastActivityDate: today,
  };
}

function addAchievement(transaction, userRef, badge, earnedAtMillis) {
  const id = badge.toLowerCase().replace(/\s+/g, "_");
  transaction.set(userRef.collection("achievements").doc(id), {
    badge,
    earnedAtMillis,
    updatedAt: FieldValue.serverTimestamp(),
  }, { merge: true });
}

function addLevelAchievement(transaction, userRef, level, earnedAtMillis) {
  if (level > 1) {
    addAchievement(transaction, userRef, `Level ${level}`, earnedAtMillis);
  }
}

function numberValue(value) {
  return typeof value === "number" ? value : 0;
}

function todayKey() {
  return new Date().toISOString().slice(0, 10);
}

function offsetDayKey(offset) {
  const day = new Date();
  day.setUTCDate(day.getUTCDate() + offset);
  return day.toISOString().slice(0, 10);
}
