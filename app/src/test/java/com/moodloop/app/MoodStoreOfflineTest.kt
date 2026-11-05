package com.moodloop.app

import android.app.Activity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.json.JSONObject

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class MoodStoreOfflineTest {
    private lateinit var activity: Activity
    private lateinit var store: MoodStore

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        activity.getSharedPreferences("moodloop_store", Activity.MODE_PRIVATE)
            .edit().clear().commit()
        store = MoodStore(activity)
    }

    @Test
    fun moodIsSavedLocallyAndAwardsPoints() {
        val mood = store.addMood("offline-user", "Calm", "Taking a quiet break")

        assertEquals(1, store.moods("offline-user").length())
        assertEquals("Calm", store.latestMood("offline-user")?.optString("emotion"))
        assertEquals(10, store.points("offline-user"))
        assertTrue(mood.optString("id").isNotBlank())
    }

    @Test
    fun queuedOfflineMoodSurvivesStoreRecreationAndCanBeCompleted() {
        val userId = "offline-user"
        val mood = store.addMood(userId, "Happy", "")
        store.queueOfflineAction(userId, "mood", mood)
        store.queueOfflineAction(userId, "mood", JSONObject(mood.toString()))

        val reopenedStore = MoodStore(activity)
        val pending = reopenedStore.pendingOfflineActions()
        assertEquals(1, pending.length())
        assertEquals("mood", pending.optJSONObject(0)?.optString("type"))

        reopenedStore.completeOfflineAction(pending.optJSONObject(0)!!.optString("operationId"))
        assertEquals(0, reopenedStore.pendingOfflineActions().length())
    }

    @Test
    fun reflectionIsSavedLocallyAndAwardsReflectionPoints() {
        val reflection = store.addReflection("offline-user", "  Today felt calmer.  ", "Calm", false)

        assertEquals(1, store.reflections("offline-user").length())
        assertEquals("Today felt calmer.", reflection.optString("text"))
        assertEquals("Private", reflection.optString("sharing"))
        assertEquals(15, store.points("offline-user"))
    }
}
