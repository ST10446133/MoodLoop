package com.moodloop.app

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class FirestoreRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun ensureUserProfile(userId: String, displayName: String, email: String) {
        val profile = mapOf(
            "displayName" to displayName.trim().ifEmpty { "there" },
            "email" to email.trim().lowercase(Locale.US),
            "updatedAt" to FieldValue.serverTimestamp(),
        )
        db.collection("users")
            .document(userId)
            .set(profile, SetOptions.merge())
    }

    fun syncUserStats(
        userId: String,
        displayName: String,
        email: String,
        points: Int,
        level: Int,
        currentStreak: Int,
        bestStreak: Int,
        moodCount: Int,
        reflectionCount: Int,
        achievements: JSONArray,
    ) {
        val userRef = db.collection("users").document(userId)
        userRef.set(
            mapOf(
                "displayName" to displayName.trim().ifEmpty { "there" },
                "email" to email.trim().lowercase(Locale.US),
                "points" to points,
                "level" to level,
                "currentStreak" to currentStreak,
                "bestStreak" to bestStreak,
                "moodCount" to moodCount,
                "reflectionCount" to reflectionCount,
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
            SetOptions.merge(),
        )

        for (i in 0 until achievements.length()) {
            val achievement = achievements.optJSONObject(i)
            val badge = achievement.optString("badge")
            if (badge.isNotBlank()) {
                userRef.collection("achievements")
                    .document(badge.lowercase(Locale.US).replace(" ", "_"))
                    .set(
                        mapOf(
                            "badge" to badge,
                            "earnedAtMillis" to achievement.optLong("time"),
                            "updatedAt" to FieldValue.serverTimestamp(),
                        ),
                        SetOptions.merge(),
                    )
            }
        }
    }

    fun saveMood(userId: String, mood: JSONObject, callback: SaveCallback) {
        val moodData = mapOf(
            "id" to mood.optString("id"),
            "emotion" to mood.optString("emotion"),
            "note" to mood.optString("note").trim(),
            "createdAtMillis" to mood.optLong("time"),
            "createdAt" to FieldValue.serverTimestamp(),
            "pointsAwarded" to 10,
        )

        val userRef = db.collection("users").document(userId)
        userRef.collection("moods")
            .document(mood.optString("id"))
            .set(moodData, SetOptions.merge())
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener { callback.onError(it.message ?: "Mood could not be saved to Firestore") }
    }

    fun saveReflection(userId: String, reflection: JSONObject, callback: SaveCallback) {
        val shared = reflection.optString("sharing") == "Shared anonymously"
        val reflectionData = mapOf(
            "id" to reflection.optString("id"),
            "text" to reflection.optString("text").trim(),
            "emotion" to reflection.optString("emotion"),
            "sharing" to reflection.optString("sharing"),
            "sharedAnonymously" to shared,
            "createdAtMillis" to reflection.optLong("time"),
            "createdAt" to FieldValue.serverTimestamp(),
            "helped" to reflection.optInt("helped"),
            "pointsAwarded" to 15,
        )

        val reflectionId = reflection.optString("id")
        val userRef = db.collection("users").document(userId)
        userRef.collection("reflections")
            .document(reflectionId)
            .set(reflectionData, SetOptions.merge())
            .addOnSuccessListener {
                if (shared) {
                    saveWallPost(userId, reflection, callback)
                } else {
                    callback.onSuccess()
                }
            }
            .addOnFailureListener { callback.onError(it.message ?: "Reflection could not be saved to Firestore") }
    }

    fun saveReminder(userId: String, enabled: Boolean, time: String, callback: SaveCallback? = null) {
        db.collection("users")
            .document(userId)
            .collection("settings")
            .document("reminders")
            .set(
                mapOf(
                    "enabled" to enabled,
                    "time" to time.trim().ifEmpty { "18:00" },
                    "updatedAt" to FieldValue.serverTimestamp(),
                ),
                SetOptions.merge(),
            )
            .addOnSuccessListener { callback?.onSuccess() }
            .addOnFailureListener { callback?.onError(it.message ?: "Reminder settings could not be saved to Firestore") }
    }

    fun saveFcmToken(userId: String, token: String, callback: SaveCallback? = null) {
        if (userId.isBlank() || token.isBlank()) {
            callback?.onError("Notification token was empty")
            return
        }
        db.collection("users")
            .document(userId)
            .collection("fcmTokens")
            .document(token)
            .set(
                mapOf(
                    "token" to token,
                    "platform" to "android",
                    "updatedAt" to FieldValue.serverTimestamp(),
                ),
                SetOptions.merge(),
            )
            .addOnSuccessListener { callback?.onSuccess() }
            .addOnFailureListener { callback?.onError(it.message ?: "Notification token could not be saved") }
    }

    fun saveHelpedClick(userId: String, postId: String) {
        db.collection("users")
            .document(userId)
            .collection("helpedPosts")
            .document(postId)
            .set(
                mapOf(
                    "postId" to postId,
                    "clickedAt" to FieldValue.serverTimestamp(),
                ),
                SetOptions.merge(),
            )

        db.collection("wallPosts")
            .document(postId)
            .set(
                mapOf(
                    "helped" to FieldValue.increment(1),
                    "updatedAt" to FieldValue.serverTimestamp(),
                ),
                SetOptions.merge(),
            )
    }

    fun loadUserData(userId: String, callback: UserDataCallback) {
        val userRef = db.collection("users").document(userId)
        Tasks.whenAllSuccess<Any>(
            userRef.get(),
            userRef.collection("moods").orderBy("createdAtMillis", Query.Direction.ASCENDING).get(),
            userRef.collection("reflections").orderBy("createdAtMillis", Query.Direction.ASCENDING).get(),
            userRef.collection("achievements").orderBy("earnedAtMillis", Query.Direction.ASCENDING).get(),
            userRef.collection("settings").document("reminders").get(),
        )
            .addOnSuccessListener { results ->
                val profileDoc = results[0] as DocumentSnapshot
                val moods = results[1] as QuerySnapshot
                val reflections = results[2] as QuerySnapshot
                val achievements = results[3] as QuerySnapshot
                val reminder = results[4] as DocumentSnapshot

                callback.onSuccess(
                    profile = profileDoc.toJson(),
                    moods = moods.toJsonArray(::moodDocumentToJson),
                    reflections = reflections.toJsonArray(::reflectionDocumentToJson),
                    achievements = achievements.toJsonArray(::achievementDocumentToJson),
                    reminder = reminder.toReminderJson(),
                )
            }
            .addOnFailureListener { callback.onError(it.message ?: "Could not load Firestore data") }
    }

    fun loadWallPosts(callback: WallPostsCallback) {
        db.collection("wallPosts")
            .orderBy("createdAtMillis", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .addOnSuccessListener { snapshot -> callback.onSuccess(snapshot.toJsonArray(::wallPostDocumentToJson)) }
            .addOnFailureListener { callback.onError(it.message ?: "Could not load Mood Wall posts") }
    }

    private fun saveWallPost(userId: String, reflection: JSONObject, callback: SaveCallback) {
        val postId = reflection.optString("id")
        val post = mapOf(
            "id" to postId,
            "reflectionId" to postId,
            "ownerId" to userId,
            "emotion" to reflection.optString("emotion"),
            "text" to reflection.optString("text").trim(),
            "createdAtMillis" to reflection.optLong("time"),
            "createdAt" to FieldValue.serverTimestamp(),
            "helped" to reflection.optInt("helped"),
            "anonymous" to true,
        )

        db.collection("wallPosts")
            .document(postId)
            .set(post, SetOptions.merge())
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener { callback.onError(it.message ?: "Mood Wall post could not be saved to Firestore") }
    }

    private fun DocumentSnapshot.toJson(): JSONObject = JSONObject().apply {
        put("displayName", getString("displayName").orEmpty())
        put("email", getString("email").orEmpty())
        put("points", getLong("points")?.toInt() ?: 0)
        put("level", getLong("level")?.toInt() ?: 1)
        put("currentStreak", getLong("currentStreak")?.toInt() ?: 0)
        put("bestStreak", getLong("bestStreak")?.toInt() ?: 0)
    }

    private fun QuerySnapshot.toJsonArray(mapper: (DocumentSnapshot) -> JSONObject): JSONArray = JSONArray().apply {
        documents.forEach { put(mapper(it)) }
    }

    private fun moodDocumentToJson(doc: DocumentSnapshot): JSONObject = JSONObject().apply {
        put("id", doc.getString("id").ifEmptyDocId(doc.id))
        put("emotion", doc.getString("emotion").orEmpty())
        put("note", doc.getString("note").orEmpty())
        put("time", doc.getLong("createdAtMillis") ?: 0L)
    }

    private fun reflectionDocumentToJson(doc: DocumentSnapshot): JSONObject = JSONObject().apply {
        put("id", doc.getString("id").ifEmptyDocId(doc.id))
        put("text", doc.getString("text").orEmpty())
        put("emotion", doc.getString("emotion").orEmpty().ifEmpty { "Neutral" })
        put("sharing", doc.getString("sharing").orEmpty().ifEmpty {
            if (doc.getBoolean("sharedAnonymously") == true) "Shared anonymously" else "Private"
        })
        put("time", doc.getLong("createdAtMillis") ?: 0L)
        put("helped", doc.getLong("helped")?.toInt() ?: 0)
    }

    private fun achievementDocumentToJson(doc: DocumentSnapshot): JSONObject = JSONObject().apply {
        put("badge", doc.getString("badge").orEmpty().ifEmpty { doc.id })
        put("time", doc.getLong("earnedAtMillis") ?: 0L)
    }

    private fun wallPostDocumentToJson(doc: DocumentSnapshot): JSONObject = JSONObject().apply {
        put("id", doc.getString("id").ifEmptyDocId(doc.id))
        put("text", doc.getString("text").orEmpty())
        put("emotion", doc.getString("emotion").orEmpty().ifEmpty { "Neutral" })
        put("time", doc.getLong("createdAtMillis") ?: 0L)
        put("helped", doc.getLong("helped")?.toInt() ?: 0)
    }

    private fun DocumentSnapshot.toReminderJson(): JSONObject = JSONObject().apply {
        put("enabled", getBoolean("enabled") ?: false)
        put("time", getString("time").orEmpty().ifEmpty { "18:00" })
    }

    private fun String?.ifEmptyDocId(id: String): String = if (isNullOrBlank()) id else this

    interface SaveCallback {
        fun onSuccess()
        fun onError(message: String)
    }

    interface UserDataCallback {
        fun onSuccess(profile: JSONObject, moods: JSONArray, reflections: JSONArray, achievements: JSONArray, reminder: JSONObject)
        fun onError(message: String)
    }

    interface WallPostsCallback {
        fun onSuccess(posts: JSONArray)
        fun onError(message: String)
    }
}
