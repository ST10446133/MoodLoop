package com.moodloop.app

import android.app.Activity
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

data class StoreResult(val ok: Boolean, val message: String)
data class Trend(val label: String, val count: Int)

class MoodStore(activity: Activity) {
    private val prefs = activity.getSharedPreferences("moodloop_store", Activity.MODE_PRIVATE)
    private val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun sessionUser(): String? = prefs.getString("session", null)
    fun logout() {
        prefs.edit().remove("session").apply()
    }

    fun register(displayName: String, username: String, email: String, password: String, confirm: String): StoreResult {
        val id = username.trim().lowercase(Locale.US)
        val mail = email.trim().lowercase(Locale.US)
        if (displayName.trim().isEmpty() || id.isEmpty() || mail.isEmpty() || password.isEmpty()) return StoreResult(false, "Please complete all required fields")
        if (!mail.contains("@") || !mail.contains(".")) return StoreResult(false, "Please enter a valid email address")
        if (password != confirm) return StoreResult(false, "Passwords do not match")
        val users = users()
        for (i in 0 until users.length()) {
            val user = users.optJSONObject(i)
            if (user.optString("username") == id || user.optString("email") == mail) return StoreResult(false, "Username or email already exists")
        }
        val salt = UUID.randomUUID().toString()
        users.put(JSONObject()
            .put("id", id)
            .put("displayName", displayName.trim())
            .put("username", id)
            .put("email", mail)
            .put("salt", salt)
            .put("passwordHash", hash(salt + password)))
        putArray("users", users)
        putObj("profile_$id", JSONObject().put("points", 0).put("level", 1))
        putObj("streak_$id", JSONObject().put("current", 0).put("longest", 0).put("last", ""))
        putObj("reminder_$id", JSONObject().put("enabled", false).put("time", "18:00"))
        prefs.edit().putString("session", id).apply()
        return StoreResult(true, "Account created")
    }

    fun login(identity: String, password: String): String? {
        val value = identity.trim().lowercase(Locale.US)
        val users = users()
        for (i in 0 until users.length()) {
            val user = users.optJSONObject(i)
            if (user.optString("username") == value || user.optString("email") == value) {
                if (user.optString("passwordHash") == hash(user.optString("salt") + password)) {
                    prefs.edit().putString("session", user.optString("id")).apply()
                    return user.optString("id")
                }
            }
        }
        return null
    }

    fun user(userId: String): JSONObject {
        val users = users()
        for (i in 0 until users.length()) {
            val user = users.optJSONObject(i)
            if (user.optString("id") == userId) return user
        }
        return JSONObject()
    }

    fun ensureFirebaseUser(userId: String?, displayName: String?, email: String?) {
        if (userId.isNullOrBlank()) return
        val name = displayName?.trim()?.takeIf { it.isNotEmpty() } ?: "there"
        val mail = email?.trim()?.lowercase(Locale.US).orEmpty()
        val users = users()
        for (i in 0 until users.length()) {
            val user = users.optJSONObject(i)
            if (user.optString("id") == userId) {
                val existingName = user.optString("displayName").trim()
                val displayNameToKeep = existingName.takeIf { it.isNotEmpty() && it != "there" } ?: name
                user.put("displayName", displayNameToKeep).put("username", userId).put("email", mail)
                putArray("users", users)
                prefs.edit().putString("session", userId).apply()
                return
            }
        }
        users.put(JSONObject().put("id", userId).put("displayName", name).put("username", userId).put("email", mail))
        putArray("users", users)
        putObj("profile_$userId", JSONObject().put("points", 0).put("level", 1))
        putObj("streak_$userId", JSONObject().put("current", 0).put("longest", 0).put("last", ""))
        putObj("reminder_$userId", JSONObject().put("enabled", false).put("time", "18:00"))
        prefs.edit().putString("session", userId).apply()
    }

    fun importFromFirestore(
        userId: String,
        profile: JSONObject,
        moods: JSONArray,
        reflections: JSONArray,
        achievements: JSONArray,
        reminder: JSONObject,
    ) {
        val existing = user(userId)
        val displayName = profile.optString("displayName").ifEmpty { existing.optString("displayName", "there") }
        val email = profile.optString("email").ifEmpty { existing.optString("email") }
        ensureFirebaseUser(userId, displayName, email)
        putObj(
            "profile_$userId",
            JSONObject()
                .put("points", profile.optInt("points", points(userId)))
                .put("level", profile.optInt("level", level(userId))),
        )
        putObj(
            "streak_$userId",
            JSONObject()
                .put("current", profile.optInt("currentStreak", currentStreak(userId)))
                .put("longest", profile.optInt("bestStreak", longestStreak(userId)))
                .put("last", obj("streak_$userId").optString("last")),
        )
        putArray("moods_$userId", moods)
        putArray("reflections_$userId", reflections)
        putArray("achievements_$userId", achievements)
        putObj(
            "reminder_$userId",
            JSONObject()
                .put("enabled", reminder.optBoolean("enabled"))
                .put("time", reminder.optString("time", "18:00")),
        )
    }

    fun updateDisplayName(userId: String, displayName: String) {
        val users = users()
        for (i in 0 until users.length()) {
            val user = users.optJSONObject(i)
            if (user.optString("id") == userId) user.put("displayName", displayName.trim())
        }
        putArray("users", users)
    }

    fun changePassword(userId: String, oldPass: String, newPass: String, confirm: String): StoreResult {
        val users = users()
        for (i in 0 until users.length()) {
            val user = users.optJSONObject(i)
            if (user.optString("id") == userId) {
                if (user.has("passwordHash") && user.optString("passwordHash") != hash(user.optString("salt") + oldPass)) return StoreResult(false, "Current password is incorrect")
                if (newPass.trim().isEmpty() || newPass != confirm) return StoreResult(false, "New passwords do not match")
                val salt = UUID.randomUUID().toString()
                user.put("salt", salt).put("passwordHash", hash(salt + newPass))
            }
        }
        putArray("users", users)
        return StoreResult(true, "Password changed successfully")
    }

    fun deleteAccount(userId: String) {
        putArray("users", filterOut(users()) { it.optString("id") == userId })
        putArray("moods_$userId", JSONArray())
        putArray("reflections_$userId", JSONArray())
        putArray("achievements_$userId", JSONArray())
        prefs.edit().remove("profile_$userId").remove("streak_$userId").remove("reminder_$userId").remove("session").apply()
    }

    fun addMood(userId: String, emotion: String, note: String): JSONObject {
        val moods = moods(userId)
        val mood = JSONObject().put("id", UUID.randomUUID().toString()).put("emotion", emotion).put("note", note).put("time", System.currentTimeMillis())
        moods.put(mood)
        putArray("moods_$userId", moods)
        addPoints(userId, 10)
        checkBadge(userId, "First mood")
        updateStreak(userId)
        return mood
    }

    fun moods(userId: String): JSONArray = array("moods_$userId")
    fun latestMood(userId: String): JSONObject? = moods(userId).let { if (it.length() == 0) null else it.optJSONObject(it.length() - 1) }

    fun addReflection(userId: String, text: String, mood: String, shared: Boolean): JSONObject {
        val reflections = reflections(userId)
        val reflection = JSONObject()
            .put("id", UUID.randomUUID().toString())
            .put("text", text.trim())
            .put("emotion", if (mood == "No mood") "Neutral" else mood)
            .put("sharing", if (shared) "Shared anonymously" else "Private")
            .put("time", System.currentTimeMillis())
            .put("helped", 0)
        reflections.put(reflection)
        putArray("reflections_$userId", reflections)
        addPoints(userId, 15)
        checkBadge(userId, "First reflection")
        if (shared) checkBadge(userId, "Shared support")
        return reflection
    }

    fun reflections(userId: String): JSONArray = array("reflections_$userId")
    fun reflection(userId: String, id: String): JSONObject? {
        val items = reflections(userId)
        for (i in 0 until items.length()) if (items.optJSONObject(i).optString("id") == id) return items.optJSONObject(i)
        return null
    }

    fun updateReflection(userId: String, id: String, text: String) {
        val items = reflections(userId)
        for (i in 0 until items.length()) if (items.optJSONObject(i).optString("id") == id) items.optJSONObject(i).put("text", text.trim())
        putArray("reflections_$userId", items)
    }

    fun deleteReflection(userId: String, id: String) {
        putArray("reflections_$userId", filterOut(reflections(userId)) { it.optString("id") == id })
    }

    fun wall(filter: String): JSONArray {
        val posts = JSONArray()
        val users = users()
        for (i in 0 until users.length()) {
            val userId = users.optJSONObject(i).optString("id")
            val refs = reflections(userId)
            for (r in 0 until refs.length()) {
                val item = refs.optJSONObject(r)
                if (item.optString("sharing") == "Shared anonymously" && (filter == "All" || item.optString("emotion") == filter)) posts.put(item)
            }
        }
        return posts
    }

    fun helped(postId: String) {
        val users = users()
        for (i in 0 until users.length()) {
            val userId = users.optJSONObject(i).optString("id")
            val refs = reflections(userId)
            var changed = false
            for (r in 0 until refs.length()) {
                val item = refs.optJSONObject(r)
                if (item.optString("id") == postId) {
                    item.put("helped", item.optInt("helped") + 1)
                    changed = true
                }
            }
            if (changed) putArray("reflections_$userId", refs)
        }
    }

    fun sevenDayTrend(userId: String): List<Trend> {
        val moods = moods(userId)
        val trends = ArrayList<Trend>()
        for (offset in 6 downTo 0) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -offset)
            val key = dateKey.format(cal.time)
            var count = 0
            for (i in 0 until moods.length()) if (dateKey.format(Date(moods.optJSONObject(i).optLong("time"))) == key) count++
            trends.add(Trend(SimpleDateFormat("EEE dd", Locale.getDefault()).format(cal.time), count))
        }
        return trends
    }

    fun addPoints(userId: String, amount: Int) {
        val profile = profile(userId)
        val points = profile.optInt("points") + amount
        profile.put("points", points).put("level", points / 100 + 1)
        putObj("profile_$userId", profile)
        if (points >= 100) checkBadge(userId, "Level ${points / 100 + 1}")
    }

    fun points(userId: String): Int = profile(userId).optInt("points")
    fun level(userId: String): Int = profile(userId).optInt("level", 1)
    fun nextLevelAt(userId: String): Int = level(userId) * 100
    fun achievements(userId: String): JSONArray = array("achievements_$userId")
    fun currentStreak(userId: String): Int = obj("streak_$userId").optInt("current")
    fun longestStreak(userId: String): Int = obj("streak_$userId").optInt("longest")
    fun reminder(userId: String): JSONObject = obj("reminder_$userId")
    fun setReminder(userId: String, enabled: Boolean, time: String) {
        putObj("reminder_$userId", JSONObject().put("enabled", enabled).put("time", time.trim().ifEmpty { "18:00" }))
    }

    private fun updateStreak(userId: String) {
        val today = dateKey.format(Date())
        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val yesterday = dateKey.format(yesterdayCal.time)
        val streak = obj("streak_$userId")
        if (streak.optString("last") == today) return
        val current = if (streak.optString("last") == yesterday) streak.optInt("current") + 1 else 1
        streak.put("current", current).put("longest", maxOf(current, streak.optInt("longest"))).put("last", today)
        putObj("streak_$userId", streak)
        if (current == 3) { addPoints(userId, 25); checkBadge(userId, "3-day streak") }
        if (current == 7) { addPoints(userId, 50); checkBadge(userId, "7-day streak") }
    }

    private fun checkBadge(userId: String, badge: String) {
        val achievements = achievements(userId)
        for (i in 0 until achievements.length()) if (achievements.optJSONObject(i).optString("badge") == badge) return
        achievements.put(JSONObject().put("badge", badge).put("time", System.currentTimeMillis()))
        putArray("achievements_$userId", achievements)
    }

    private fun users(): JSONArray = array("users")
    private fun profile(userId: String): JSONObject = obj("profile_$userId").let { if (it.length() == 0) JSONObject().put("points", 0).put("level", 1) else it }
    private fun array(key: String): JSONArray = try { JSONArray(prefs.getString(key, "[]") ?: "[]") } catch (_: Exception) { JSONArray() }
    private fun obj(key: String): JSONObject = try { JSONObject(prefs.getString(key, "{}") ?: "{}") } catch (_: Exception) { JSONObject() }
    private fun putArray(key: String, value: JSONArray) = prefs.edit().putString(key, value.toString()).apply()
    private fun putObj(key: String, value: JSONObject) = prefs.edit().putString(key, value.toString()).apply()

    private fun filterOut(source: JSONArray, reject: (JSONObject) -> Boolean): JSONArray {
        val kept = JSONArray()
        for (i in 0 until source.length()) {
            val item = source.optJSONObject(i)
            if (!reject(item)) kept.put(item)
        }
        return kept
    }

    private fun hash(value: String): String = try {
        MessageDigest.getInstance("SHA-256").digest(value.toByteArray()).joinToString("") { "%02x".format(it) }
    } catch (_: Exception) {
        ""
    }
}
