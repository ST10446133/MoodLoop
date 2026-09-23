package com.moodloop.app

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : RoundedFontActivity() {
    private lateinit var store: MoodStore
    private lateinit var auth: FirebaseAuthRepository
    private lateinit var firestore: FirestoreRepository
    private var sessionUser: String? = null
    private val googleSignInRequest = 4301
    private var googleSignInMode: GoogleSignInMode = GoogleSignInMode.SignIn

    private val emotions = listOf("Happy", "Calm", "Excited", "Sad", "Angry", "Anxious", "Stressed", "Tired", "Neutral")
    private val affirmations = listOf(
        "Your feelings are valid, and this moment can be held gently.",
        "Small check-ins count. You showed up for yourself today.",
        "You do not have to solve everything at once.",
        "A quiet breath is still progress.",
        "Today can be simple and still meaningful.",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = MoodStore(this)
        auth = FirebaseAuthRepository()
        firestore = FirestoreRepository()
        sessionUser = auth.currentUserId()
        if (sessionUser != null) {
            val userId = sessionUser ?: return
            store.ensureFirebaseUser(userId, auth.currentUserName(), auth.currentUserEmail())
            firestore.ensureUserProfile(userId, auth.currentUserName(), auth.currentUserEmail())
            syncFirestoreUser(userId)
            setupNotifications(userId)
        } else {
            sessionUser = store.sessionUser()
        }
        if (sessionUser == null) showWelcome() else showHome()
    }

    private fun showWelcome() {
        val screen = vertical().apply { setBackgroundColor(0xFFFFFFFF.toInt()) }
        val hero = WelcomeHero().apply { setPadding(dp(20), 0, dp(20), 0) }
        val heroContent = vertical().apply { gravity = Gravity.CENTER_HORIZONTAL }
        hero.addView(heroContent, FrameLayout.LayoutParams(-1, -2, Gravity.CENTER).apply {
            setMargins(0, 0, 0, dp(8))
        })

        heroContent.addView(appMark(), margins(LinearLayout.LayoutParams(dp(64), dp(64)), 0, 0, 0, 22))
        heroContent.addView(TextView(this).apply {
            text = "MoodLoop"
            textSize = 32f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(0xFF4A3FA2.toInt())
        }, LinearLayout.LayoutParams(-1, -2))
        heroContent.addView(TextView(this).apply {
            text = "A calm, anonymous space for student reflection.\nReflect. Connect. Heal."
            textSize = 16f
            gravity = Gravity.CENTER
            setLineSpacing(dp(2).toFloat(), 1.0f)
            setTextColor(0xFF7D71B7.toInt())
        }, margins(LinearLayout.LayoutParams(-1, -2), 0, 8, 0, 0))

        screen.addView(hero, LinearLayout.LayoutParams(-1, 0, 1f))
        val actionPanel = vertical().apply {
            setPadding(dp(20), dp(22), dp(20), dp(16))
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(0xFFFFFFFF.toInt())
        }
        screen.addView(actionPanel, LinearLayout.LayoutParams(-1, dp(156)))
        actionPanel.addView(welcomeButton("Get started", true) { showRegister() })
        actionPanel.addView(welcomeButton("I already have an account", false) { showLogin() })
        setContentView(screen)
    }

    private fun showRegister() {
        setContentView(CreateAccountView(this, object : CreateAccountView.Actions {
            override fun createAccount(form: CreateAccountView.FormData) {
                if (form.password != form.confirmPassword) {
                    toast("Passwords do not match")
                    return
                }
                auth.register(form.name, form.email, form.password, object : FirebaseAuthRepository.AuthCallback {
                    override fun onSuccess(userId: String, email: String, displayName: String) {
                        sessionUser = userId
                        store.ensureFirebaseUser(userId, displayName, email)
                        firestore.ensureUserProfile(userId, displayName, email)
                        syncFirestoreUser(userId)
                        setupNotifications(userId)
                        toast("Account created successfully")
                        showHome()
                    }

                    override fun onError(message: String) = toast(message)
                })
            }

            override fun googleSignIn() = startGoogleSignIn(GoogleSignInMode.Register)
            override fun signIn() = showLogin()
        }))
    }

    private fun showLogin() {
        setContentView(SignInView(this, object : SignInView.Actions {
            override fun signIn(form: SignInView.FormData) {
                auth.signIn(form.email, form.password, object : FirebaseAuthRepository.AuthCallback {
                    override fun onSuccess(userId: String, email: String, displayName: String) {
                        sessionUser = userId
                        store.ensureFirebaseUser(userId, displayName, email)
                        firestore.ensureUserProfile(userId, displayName, email)
                        syncFirestoreUser(userId)
                        setupNotifications(userId)
                        toast("Signed in successfully")
                        showHome()
                    }

                    override fun onError(message: String) = toast(message)
                })
            }

            override fun forgotPassword(email: String) {
                auth.sendPasswordReset(email, object : FirebaseAuthRepository.PasswordResetCallback {
                    override fun onSuccess(message: String) = toastLong(message)
                    override fun onError(message: String) = toast(message)
                })
            }

            override fun googleSignIn() = startGoogleSignIn(GoogleSignInMode.SignIn)
            override fun register() = showRegister()
        }))
    }

    @Deprecated("Used for Google Sign-In result handling.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != googleSignInRequest) return

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken.isNullOrBlank()) {
                toast("Google sign-in setup is missing. Check Firebase Console setup.")
                return
            }
            auth.signInWithGoogle(idToken, object : FirebaseAuthRepository.GoogleAuthCallback {
                override fun onSuccess(userId: String, email: String, displayName: String, isNewUser: Boolean) {
                    sessionUser = userId
                    store.ensureFirebaseUser(userId, displayName.ifBlank { "there" }, email)
                    firestore.ensureUserProfile(userId, displayName.ifBlank { "there" }, email)
                    syncFirestoreUser(userId)
                    setupNotifications(userId)
                    toast(if (googleSignInMode == GoogleSignInMode.Register) "Account created successfully" else "Signed in successfully")
                    showHome()
                }

                override fun onError(message: String) = toast(message)
            })
        } catch (error: ApiException) {
            return
        }
    }

    private fun startGoogleSignIn(mode: GoogleSignInMode) {
        googleSignInMode = mode
        val clientId = googleWebClientId()
        if (clientId.isBlank()) {
            return
        }

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(this, options)
        client.signOut().addOnCompleteListener {
            startActivityForResult(client.signInIntent, googleSignInRequest)
        }
    }

    private fun googleWebClientId(): String {
        val generatedId = resources.getIdentifier("default_web_client_id", "string", packageName)
        if (generatedId != 0) {
            val generated = getString(generatedId)
            if (generated.isNotBlank()) return generated
        }
        val fallbackId = resources.getIdentifier("google_web_client_id", "string", packageName)
        return if (fallbackId == 0) "" else getString(fallbackId)
    }

    private enum class GoogleSignInMode {
        Register,
        SignIn,
    }

    private fun showHome() {
        val userId = requireUser()
        setupNotifications(userId)
        val user = store.user(userId)
        val latest = store.latestMood(userId)
        val points = store.points(userId)
        val nextLevelAt = store.nextLevelAt(userId).coerceAtLeast(1)
        val progressPercent = ((points.toFloat() / nextLevelAt.toFloat()) * 100).toInt().coerceIn(0, 100)
        val latestEmotion = latest?.optString("emotion").orEmpty()
        setContentView(HomePageView(this, HomePageView.State(
            displayName = user.optString("displayName", "there"),
            streakDays = store.currentStreak(userId),
            bestStreakDays = store.longestStreak(userId),
            points = points,
            level = store.level(userId),
            nextLevelAt = nextLevelAt,
            progressPercent = progressPercent,
            pointsToNextLevel = (nextLevelAt - points).coerceAtLeast(0),
            latestMoodIcon = moodIcon(latestEmotion),
            latestMoodTitle = latestEmotion.ifEmpty { "No mood yet" },
            latestMoodNote = latest?.optString("note")?.ifEmpty { "Log a mood to start tracking." } ?: "Log a mood to start tracking.",
            latestMoodDate = latest?.optLong("time")?.takeIf { it > 0L }?.let { shortDate(it) } ?: "No entries yet",
        ), object : HomePageView.Actions {
            override fun home() = showHome()
            override fun logMood() = showLogMood("Happy", "")
            override fun writeReflection() = showJournal()
            override fun openMoodWall() = showMoodWall("All")
            override fun openProfile() = showProfile()
        }))
    }

    private fun showLogMood(selected: String, oldNote: String) {
        setContentView(LogMoodView(this, selected, oldNote, object : LogMoodView.Actions {
            override fun saveMood(emotion: String, note: String) {
                val userId = requireUser()
                val mood = store.addMood(userId, emotion, note)
                syncFirestoreUser(userId)
                firestore.saveMood(userId, mood, object : FirestoreRepository.SaveCallback {
                    override fun onSuccess() {
                        toast("Mood saved successfully")
                        showHome()
                    }

                    override fun onError(message: String) {
                        toast(message)
                        showHome()
                    }
                })
            }

            override fun home() = showHome()
            override fun journal() = showJournal()
            override fun wall() = showMoodWall("All")
            override fun me() = showProfile()
        }))
    }

    private fun showJournal() {
        val userId = requireUser()
        val reflections = store.reflections(userId)
        val items = mutableListOf<JournalView.Reflection>()
        for (i in reflections.length() - 1 downTo 0) {
            val item = reflections.optJSONObject(i)
            items.add(JournalView.Reflection(
                text = item.optString("text"),
                emotion = item.optString("emotion", "Neutral"),
                sharing = if (item.optString("sharing") == "Shared anonymously") "Shared" else "Private",
                timeText = shortDate(item.optLong("time")),
            ))
        }
        setContentView(JournalView(this, JournalView.State(
            reflections = items,
        ), object : JournalView.Actions {
            override fun newReflection() = showNewReflection()
            override fun home() = showHome()
            override fun log() = showLogMood("Happy", "")
            override fun wall() = showMoodWall("All")
            override fun me() = showProfile()
        }))
    }

    private fun showNewReflection() {
        setContentView(ReflectionView(this, "", object : ReflectionView.Actions {
            override fun saveReflection(text: String, emotion: String?, shareAnonymously: Boolean?) {
                if (text.trim().isEmpty()) {
                    toast("Reflection cannot be empty")
                    return
                }
                if (emotion == null) {
                    toast("Please choose an emotion")
                    return
                }
                if (shareAnonymously == null) {
                    toast("Please choose private or share anonymously")
                    return
                }
                val userId = requireUser()
                val reflection = store.addReflection(userId, text, emotion, shareAnonymously)
                syncFirestoreUser(userId)
                firestore.saveReflection(userId, reflection, object : FirestoreRepository.SaveCallback {
                    override fun onSuccess() {
                        toast("Reflection saved")
                        showJournal()
                    }

                    override fun onError(message: String) {
                        toast(message)
                        showJournal()
                    }
                })
            }

            override fun back() = showJournal()
            override fun home() = showHome()
            override fun log() = showLogMood("Happy", "")
            override fun journal() = showJournal()
            override fun wall() = showMoodWall("All")
            override fun me() = showProfile()
        }))
    }

    private fun showReflections() {
        val userId = requireUser()
        val body = input("Write a reflection", false).apply { minLines = 4 }
        val mood = spinner(listOf("No mood") + emotions)
        val share = CheckBox(this).apply { text = "Share anonymously to Mood Wall" }
        setScreen("Reflections", "Private by default. Share only when you choose.") { root ->
            root.addView(body)
            root.addView(mood)
            root.addView(share)
            root.addView(primaryButton("Save reflection") {
                if (text(body).trim().isEmpty()) {
                    toast("Reflection cannot be empty")
                } else {
                    val reflection = store.addReflection(userId, text(body), mood.selectedItem.toString(), share.isChecked)
                    syncFirestoreUser(userId)
                    firestore.saveReflection(userId, reflection, object : FirestoreRepository.SaveCallback {
                        override fun onSuccess() {
                            toast("Reflection saved")
                            showReflections()
                        }

                        override fun onError(message: String) {
                            toast(message)
                            showReflections()
                        }
                    })
                }
            })
            val list = store.reflections(userId)
            if (list.length() == 0) root.addView(emptyText("No reflections yet."))
            for (i in 0 until list.length()) {
                val item = list.optJSONObject(i)
                root.addView(itemCard(
                    "${item.optString("sharing")} - ${shortDate(item.optLong("time"))}",
                    item.optString("text"),
                    "Edit",
                    "Delete",
                    { editReflection(item.optString("id")) },
                    { confirm("Delete reflection?", "This cannot be undone.") {
                        store.deleteReflection(userId, item.optString("id"))
                        toast("Reflection deleted")
                        showReflections()
                    } },
                ))
            }
            addNav(root, "Reflections")
        }
    }

    private fun editReflection(id: String) {
        val userId = requireUser()
        val item = store.reflection(userId, id) ?: return
        val edit = input("Reflection", false).apply {
            minLines = 4
            setText(item.optString("text"))
        }
        AlertDialog.Builder(this)
            .setTitle("Edit reflection")
            .setView(edit)
            .setPositiveButton("Save") { _, _ ->
                store.updateReflection(userId, id, text(edit))
                toast("Reflection saved")
                showReflections()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showMoodWall(filter: String) {
        firestore.loadWallPosts(object : FirestoreRepository.WallPostsCallback {
            override fun onSuccess(posts: JSONArray) {
                showMoodWallWithPosts(filter, posts)
            }

            override fun onError(message: String) {
                toast(message)
                showMoodWallWithPosts(filter, store.wall("All"))
            }
        })
    }

    private fun showMoodWallWithPosts(filter: String, wallPosts: JSONArray) {
        val posts = mutableListOf<MoodWallView.Post>()
        for (i in 0 until wallPosts.length()) {
            val post = wallPosts.optJSONObject(i)
            posts.add(MoodWallView.Post(
                id = post.optString("id"),
                emotion = post.optString("emotion", "Neutral"),
                text = post.optString("text"),
                whenText = relativeTime(post.optLong("time")),
            ))
        }
        val visiblePosts = posts + sampleWallPosts()
        setContentView(MoodWallView(this, MoodWallView.State(
            filter = filter,
            posts = visiblePosts,
        ), object : MoodWallView.Actions {
            override fun helped(postId: String) {
                if (!postId.startsWith("sample-")) {
                    val userId = requireUser()
                    store.helped(postId)
                    firestore.saveHelpedClick(userId, postId)
                }
            }
            override fun home() = showHome()
            override fun log() = showLogMood("Happy", "")
            override fun journal() = showJournal()
            override fun me() = showProfile()
        }))
    }

    private fun showTracker() {
        val userId = requireUser()
        setScreen("Mood Tracker", "Previous seven days, shown without judgement.") { root ->
            root.addView(summaryCard("Total entries", "${store.moods(userId).length()} mood entries"))
            store.sevenDayTrend(userId).forEach { root.addView(barRow(it.label, it.count)) }
            store.addPoints(userId, 5)
            addNav(root, "Tracker")
        }
    }

    private fun showAchievements() {
        val userId = requireUser()
        setScreen("Achievements", "Participation badges and calm progress.") { root ->
            root.addView(summaryCard("Level", "Level ${store.level(userId)} - ${store.points(userId)} points"))
            val badges = store.achievements(userId)
            if (badges.length() == 0) root.addView(emptyText("Your first badge will appear after a mood or reflection."))
            for (i in 0 until badges.length()) {
                val badge = badges.optJSONObject(i)
                root.addView(summaryCard(badge.optString("badge"), "Earned ${shortDate(badge.optLong("time"))}"))
            }
            addNav(root, "Achievements")
        }
    }

    private fun showProfile() {
        val userId = requireUser()
        val user = store.user(userId)
        val points = store.points(userId)
        val nextLevelAt = store.nextLevelAt(userId).coerceAtLeast(1)
        val progressPercent = ((points.toFloat() / nextLevelAt.toFloat()) * 100).toInt().coerceIn(0, 100)
        val topEmotion = mostLoggedEmotion(userId)
        setContentView(ProfileView(this, ProfileView.State(
            displayName = user.optString("displayName", "there"),
            username = profileHandle(user.optString("displayName", "there"), user.optString("username"), userId),
            email = user.optString("email", auth.currentUserEmail().orEmpty()),
            initial = user.optString("displayName", "E").trim().firstOrNull()?.uppercaseChar()?.toString() ?: "E",
            level = store.level(userId),
            points = points,
            currentStreak = store.currentStreak(userId),
            bestStreak = store.longestStreak(userId),
            moodCount = store.moods(userId).length(),
            achievementCount = store.achievements(userId).length(),
            progressPercent = progressPercent,
            pointsToNextLevel = (nextLevelAt - points).coerceAtLeast(0),
            topEmotion = topEmotion.first,
            topEmotionCount = topEmotion.second,
        ), object : ProfileView.Actions {
            override fun home() = showHome()
            override fun log() = showLogMood("Happy", "")
            override fun journal() = showJournal()
            override fun wall() = showMoodWall("All")
            override fun tracker() = showProfileTracker()
            override fun badges() = showProfileBadges()
            override fun settings() = showProfileSettings()
        }))
    }

    private fun showProfileTracker() {
        val userId = requireUser()
        val trends = store.sevenDayTrend(userId).map { TrackerView.TrendItem(it.label, it.count) }
        setContentView(TrackerView(this, TrackerView.State(
            totalEntries = store.moods(userId).length(),
            trends = trends,
            maxTrendCount = trends.maxOfOrNull { it.count } ?: 1,
        ), object : TrackerView.Actions {
            override fun overview() = showProfile()
            override fun badges() = showProfileBadges()
            override fun settings() = showProfileSettings()
            override fun home() = showHome()
            override fun log() = showLogMood("Happy", "")
            override fun journal() = showJournal()
            override fun wall() = showMoodWall("All")
        }))
    }

    private fun showProfileBadges() {
        val userId = requireUser()
        val badges = store.achievements(userId)
        val items = mutableListOf<BadgeView.Badge>()
        for (i in 0 until badges.length()) {
            val badge = badges.optJSONObject(i)
            items.add(BadgeView.Badge(badge.optString("badge"), shortDate(badge.optLong("time"))))
        }
        setContentView(BadgeView(this, BadgeView.State(items), object : BadgeView.Actions {
            override fun overview() = showProfile()
            override fun tracker() = showProfileTracker()
            override fun settings() = showProfileSettings()
            override fun home() = showHome()
            override fun log() = showLogMood("Happy", "")
            override fun journal() = showJournal()
            override fun wall() = showMoodWall("All")
        }))
    }

    private fun showProfileSettings() {
        setContentView(SettingsView(this, object : SettingsView.Actions {
            override fun overview() = showProfile()
            override fun tracker() = showProfileTracker()
            override fun badges() = showProfileBadges()
            override fun editDisplayName() = this@MainActivity.editDisplayName()
            override fun changePassword() = this@MainActivity.changePassword()
            override fun reminders() = reminderDialog()
            override fun logout() {
                auth.signOut()
                store.logout()
                sessionUser = null
                showWelcome()
            }
            override fun deleteAccount() = showDeleteAccount()
            override fun home() = showHome()
            override fun log() = showLogMood("Happy", "")
            override fun journal() = showJournal()
            override fun wall() = showMoodWall("All")
        }))
    }

    private fun showDeleteAccount() {
        setContentView(DeleteAccountView(this, object : DeleteAccountView.Actions {
            override fun confirmDelete() {
                val userId = requireUser()
                auth.deleteCurrentUser(object : FirebaseAuthRepository.DeleteAccountCallback {
                    override fun onSuccess() {
                        store.deleteAccount(userId)
                        store.logout()
                        sessionUser = null
                        toast("Account deleted")
                        showWelcome()
                    }

                    override fun onError(message: String) = toastLong(message)
                })
            }

            override fun back() = showProfileSettings()
        }))
    }

    private fun editDisplayName() {
        setContentView(EditDisplayNameView(this, store.user(requireUser()).optString("displayName"), object : EditDisplayNameView.Actions {
            override fun save(name: String) {
                val cleanName = name.trim()
                if (cleanName.isEmpty()) {
                    toast("Display name cannot be empty")
                    return
                }
                auth.updateDisplayName(cleanName, object : FirebaseAuthRepository.ProfileCallback {
                    override fun onSuccess() {
                        store.updateDisplayName(requireUser(), cleanName)
                        syncFirestoreUser(requireUser())
                        toast("Profile updated")
                        showProfileSettings()
                    }

                    override fun onError(message: String) = toast(message)
                })
            }

            override fun back() = showProfileSettings()
        }))
    }

    private fun changePassword() {
        setContentView(ChangePasswordView(this, object : ChangePasswordView.Actions {
            override fun save(currentPassword: String, newPassword: String, confirmPassword: String) {
                auth.changePassword(currentPassword, newPassword, confirmPassword, object : FirebaseAuthRepository.PasswordCallback {
                    override fun onSuccess(message: String) {
                        toast(message)
                        showProfileSettings()
                    }

                    override fun onError(message: String) = toast(message)
                })
            }

            override fun back() = showProfileSettings()
        }))
    }

    private fun reminderDialog() {
        val userId = requireUser()
        val reminder = store.reminder(userId)
        setContentView(ReminderSettingsView(this, reminder.optBoolean("enabled"), reminder.optString("time", "18:00"), object : ReminderSettingsView.Actions {
            override fun save(enabled: Boolean, time: String) {
                if (enabled) requestNotificationPermissionIfNeeded()
                if (!ReminderScheduler.applyReminder(this@MainActivity, enabled, time)) {
                    toast("Please enter a valid time, like 18:30")
                    return
                }
                store.setReminder(userId, enabled, time)
                firestore.saveReminder(userId, enabled, time, object : FirestoreRepository.SaveCallback {
                    override fun onSuccess() {
                        toast(if (enabled) "Reminder saved" else "Reminder turned off")
                        showProfileSettings()
                    }

                    override fun onError(message: String) {
                        toast(message)
                        showProfileSettings()
                    }
                })
            }

            override fun back() = showProfileSettings()
        }))
    }

    private fun setScreen(title: String, subtitle: String, builder: (LinearLayout) -> Unit) {
        val scroll = ScrollView(this).apply { setBackgroundColor(0xFFF7FAF8.toInt()) }
        val root = vertical().apply { setPadding(dp(18), dp(20), dp(18), dp(24)) }
        root.addView(TextView(this).apply {
            text = title
            textSize = 30f
            setTextColor(0xFF27423D.toInt())
            typeface = Typeface.DEFAULT_BOLD
        })
        root.addView(TextView(this).apply {
            text = subtitle
            textSize = 16f
            setTextColor(0xFF5D6F6A.toInt())
            setPadding(0, dp(4), 0, dp(16))
        })
        builder(root)
        scroll.addView(root)
        setContentView(scroll)
    }

    private fun addNav(root: LinearLayout, active: String) {
        val nav = vertical()
        val rows = listOf(listOf("Home", "Log Mood", "Reflections"), listOf("Mood Wall", "Tracker", "Achievements", "Profile"))
        rows.forEach { rowItems ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            rowItems.forEach { item ->
                val button = Button(this).apply {
                    text = item
                    textSize = 12f
                    minHeight = dp(44)
                    setTextColor(if (item == active) 0xFFFFFFFF.toInt() else 0xFF335C55.toInt())
                    setBackgroundColor(if (item == active) 0xFF4F7C73.toInt() else 0xFFE4F0EC.toInt())
                    setOnClickListener { navigate(item) }
                }
                row.addView(button, margins(LinearLayout.LayoutParams(0, dp(50), 1f), 4, 4, 4, 4))
            }
            nav.addView(row)
        }
        root.addView(nav)
    }

    private fun navigate(item: String) {
        when (item) {
            "Home" -> showHome()
            "Log Mood" -> showLogMood("Happy", "")
            "Reflections" -> showReflections()
            "Mood Wall" -> showMoodWall("All")
            "Tracker" -> showTracker()
            "Achievements" -> showAchievements()
            "Profile" -> showProfile()
        }
    }

    private fun appMark(): TextView = TextView(this).apply {
        text = "🌿"
        gravity = Gravity.CENTER
        textSize = 27f
        setTextColor(0xFF82B440.toInt())
        background = gradient(0xFFF8FBFF.toInt(), 0xFFEAF2FF.toInt(), GradientDrawable.Orientation.TOP_BOTTOM, 18)
        elevation = dp(8).toFloat()
    }

    private fun welcomeButton(textValue: String, primary: Boolean, action: () -> Unit): TextView = TextView(this).apply {
        text = textValue
        gravity = Gravity.CENTER
        textSize = 16f
        typeface = Typeface.DEFAULT_BOLD
        setTextColor(if (primary) 0xFFFFFFFF.toInt() else 0xFF4D38C7.toInt())
        background = if (primary) {
            gradient(0xFF7B58B7.toInt(), 0xFF57A8DE.toInt(), GradientDrawable.Orientation.LEFT_RIGHT, 13)
        } else {
            solid(0xFFF1F4FF.toInt(), 13)
        }
        elevation = if (primary) dp(3).toFloat() else 0f
        setOnClickListener { action() }
        layoutParams = margins(LinearLayout.LayoutParams(-1, dp(48)), 0, 5, 0, 5)
    }

    private inner class WelcomeHero : FrameLayout(this) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        init {
            setWillNotDraw(false)
        }

        override fun onDraw(canvas: Canvas) {
            val width = width
            val height = height
            paint.shader = LinearGradient(0f, 0f, 0f, height.toFloat(), intArrayOf(0xFFCFC7F1.toInt(), 0xFFBFE1EE.toInt(), 0xFFC2EEDC.toInt()), null, Shader.TileMode.CLAMP)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            paint.shader = null
            paint.color = 0x24A5B2DA
            canvas.drawCircle(width * 0.92f, height * 0.24f, dp(44).toFloat(), paint)
            paint.color = 0x23A8A1CF
            canvas.drawCircle(width * 0.12f, height * 0.68f, dp(62).toFloat(), paint)
        }
    }

    private fun emotionGrid(selected: String, onPick: (String) -> Unit): GridLayout = GridLayout(this).apply {
        columnCount = 3
        emotions.forEach { emotion ->
            addView(Button(this@MainActivity).apply {
                text = "${mark(emotion)} $emotion"
                textSize = 13f
                minHeight = dp(54)
                setTextColor(if (emotion == selected) 0xFFFFFFFF.toInt() else 0xFF335C55.toInt())
                setBackgroundColor(if (emotion == selected) 0xFF4F7C73.toInt() else 0xFFEAF4F0.toInt())
                setOnClickListener { onPick(emotion) }
            }, ViewGroup.LayoutParams(dp(112), dp(58)))
        }
    }

    private fun mark(emotion: String): String = when (emotion) {
        "Happy" -> ":)"
        "Calm" -> "~"
        "Excited" -> "!"
        "Sad" -> ":("
        "Angry" -> "x"
        "Anxious" -> "?"
        "Stressed" -> "#"
        "Tired" -> "z"
        else -> "-"
    }

    private fun moodIcon(emotion: String): String = when (emotion) {
        "Happy" -> "😊"
        "Calm" -> "😌"
        "Excited" -> "🤩"
        "Sad" -> "😢"
        "Angry" -> "😠"
        "Anxious" -> "😰"
        "Stressed" -> "😤"
        "Tired" -> "😴"
        "Neutral" -> "😐"
        else -> "🤗"
    }

    private fun input(hint: String, password: Boolean): EditText = EditText(this).apply {
        this.hint = hint
        textSize = 16f
        minHeight = dp(52)
        setSingleLine(!hint.lowercase(Locale.US).contains("reflection"))
        inputType = if (password) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD else InputType.TYPE_CLASS_TEXT
        setPadding(dp(12), dp(6), dp(12), dp(6))
        layoutParams = margins(LinearLayout.LayoutParams(-1, -2), 0, 4, 0, 4)
    }

    private fun spinner(values: List<String>): Spinner = Spinner(this).apply {
        adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, values)
        minimumHeight = dp(48)
    }

    private fun primaryButton(text: String, action: () -> Unit): Button = button(text, 0xFF4F7C73.toInt(), 0xFFFFFFFF.toInt(), action)
    private fun secondaryButton(text: String, action: () -> Unit): Button = button(text, 0xFFE4F0EC.toInt(), 0xFF335C55.toInt(), action)
    private fun dangerButton(text: String, action: () -> Unit): Button = button(text, 0xFFF4D8D8.toInt(), 0xFF7F2727.toInt(), action)

    private fun button(textValue: String, bg: Int, fg: Int, action: () -> Unit): Button = Button(this).apply {
        text = textValue
        textSize = 15f
        minHeight = dp(48)
        setTextColor(fg)
        setBackgroundColor(bg)
        setOnClickListener { action() }
        layoutParams = margins(LinearLayout.LayoutParams(-1, dp(54)), 0, 6, 0, 6)
    }

    private fun solid(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun gradient(start: Int, end: Int, orientation: GradientDrawable.Orientation, radius: Int): GradientDrawable =
        GradientDrawable(orientation, intArrayOf(start, end)).apply { cornerRadius = dp(radius).toFloat() }

    private fun summaryCard(title: String, body: String): TextView = TextView(this).apply {
        text = "$title\n$body"
        textSize = 16f
        setTextColor(0xFF27423D.toInt())
        setBackgroundColor(0xFFFFFFFF.toInt())
        setPadding(dp(14), dp(12), dp(14), dp(12))
        layoutParams = margins(LinearLayout.LayoutParams(-1, -2), 0, 6, 0, 6)
    }

    private fun itemCard(title: String, body: String, left: String, right: String?, onLeft: () -> Unit, onRight: (() -> Unit)?): LinearLayout {
        val card = vertical().apply {
            setBackgroundColor(0xFFFFFFFF.toInt())
            setPadding(dp(14), dp(12), dp(14), dp(12))
            layoutParams = margins(LinearLayout.LayoutParams(-1, -2), 0, 7, 0, 7)
        }
        card.addView(TextView(this).apply {
            text = title
            textSize = 14f
            setTextColor(0xFF5D6F6A.toInt())
        })
        card.addView(TextView(this).apply {
            text = body
            textSize = 16f
            setTextColor(0xFF27423D.toInt())
            setPadding(0, dp(6), 0, dp(8))
        })
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        row.addView(secondaryButton(left, onLeft), margins(LinearLayout.LayoutParams(0, dp(50), 1f), 0, 0, 4, 0))
        if (right != null && onRight != null) row.addView(dangerButton(right, onRight), margins(LinearLayout.LayoutParams(0, dp(50), 1f), 4, 0, 0, 0))
        card.addView(row)
        return card
    }

    private fun progressLine(points: Int, next: Int): TextView {
        val filled = ((points % 100) / 10).coerceIn(0, 10)
        return summaryCard("Level progress", "[${repeat("#", filled)}${repeat("-", 10 - filled)}] $points / $next")
    }

    private fun barRow(label: String, count: Int): TextView = summaryCard(label, if (count == 0) "No entries" else "${repeat("#", count.coerceAtLeast(1).coerceAtMost(20))} $count")
    private fun emptyText(value: String): TextView = TextView(this).apply {
        text = value
        textSize = 16f
        setTextColor(0xFF5D6F6A.toInt())
        setPadding(0, dp(12), 0, dp(12))
    }

    private fun vertical(): LinearLayout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
    private fun margins(params: LinearLayout.LayoutParams, left: Int, top: Int, right: Int, bottom: Int): LinearLayout.LayoutParams {
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom))
        return params
    }

    private fun text(edit: EditText): String = edit.text.toString()
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    private fun toast(message: String) = showMoodLoopToast(message, Toast.LENGTH_SHORT)
    private fun toastLong(message: String) = showMoodLoopToast(message, Toast.LENGTH_LONG)
    private fun showMoodLoopToast(message: String, durationValue: Int) {
        val toastView = TextView(this).apply {
            text = message
            textSize = 17f
            typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) resources.getFont(R.font.ubuntu_bold) else Typeface.DEFAULT_BOLD
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(dp(18), dp(12), dp(18), dp(12))
            gravity = Gravity.CENTER
            background = solid(0xFF4A3FA2.toInt(), 18)
            maxLines = 3
        }
        Toast(this).apply {
            duration = durationValue
            view = toastView
            show()
        }
    }
    private fun confirm(title: String, message: String, yes: () -> Unit) {
        AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton("Confirm") { _, _ -> yes() }.setNegativeButton("Cancel", null).show()
    }
    private fun requireUser(): String = sessionUser ?: store.sessionUser()?.also { sessionUser = it } ?: throw IllegalStateException("No active user")
    private fun shortDate(time: Long): String = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(Date(time))
    private fun relativeTime(time: Long): String {
        val minutes = ((System.currentTimeMillis() - time).coerceAtLeast(0L) / 60000L).toInt()
        return when {
            minutes < 1 -> "just now"
            minutes < 60 -> "${minutes}m ago"
            minutes < 1440 -> "${minutes / 60}h ago"
            else -> "${minutes / 1440}d ago"
        }
    }

    private fun mostLoggedEmotion(userId: String): Pair<String, Int> {
        val moods = store.moods(userId)
        val counts = mutableMapOf<String, Int>()
        for (i in 0 until moods.length()) {
            val emotion = moods.optJSONObject(i).optString("emotion", "Neutral")
            counts[emotion] = (counts[emotion] ?: 0) + 1
        }
        val top = counts.maxByOrNull { it.value }
        return if (top == null) "Neutral" to 0 else top.key to top.value
    }

    private fun syncFirestoreUser(userId: String) {
        val user = store.user(userId)
        firestore.syncUserStats(
            userId = userId,
            displayName = user.optString("displayName", auth.currentUserName()).ifEmpty { "there" },
            email = user.optString("email", auth.currentUserEmail()),
            points = store.points(userId),
            level = store.level(userId),
            currentStreak = store.currentStreak(userId),
            bestStreak = store.longestStreak(userId),
            moodCount = store.moods(userId).length(),
            reflectionCount = store.reflections(userId).length(),
            achievements = store.achievements(userId),
        )
    }

    private fun setupNotifications(userId: String) {
        val firebaseUserId = auth.currentUserId()
        if (firebaseUserId == null) {
            toastLong("Notification setup needs Firebase sign-in. Log out and sign in again.")
            return
        }
        requestNotificationPermissionIfNeeded()
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                firestore.saveFcmToken(firebaseUserId, token)
            }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return
        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
    }

    private fun profileHandle(displayName: String, username: String, userId: String): String {
        val fromName = displayName.lowercase(Locale.US).filter { it.isLetterOrDigit() }
        if (fromName.isNotBlank() && fromName != "there") return fromName.take(18)
        val cleanUsername = username.lowercase(Locale.US).filter { it.isLetterOrDigit() }
        if (cleanUsername.length in 3..18) return cleanUsername
        return "user${userId.take(4).lowercase(Locale.US)}"
    }

    private fun sampleWallPosts(): List<MoodWallView.Post> {
        val samples = listOf(
            MoodWallView.Post("sample-1", "Stressed", "Assignments are getting hectic.", "just now"),
            MoodWallView.Post("sample-2", "Happy", "I finished a task I had been avoiding for weeks.", "18m ago"),
            MoodWallView.Post("sample-3", "Calm", "Made coffee, opened my notes, and it clicked.", "1h ago"),
            MoodWallView.Post("sample-4", "Sad", "Today felt heavy, but writing it down helped a little.", "4h ago"),
            MoodWallView.Post("sample-5", "Anxious", "I was nervous before class, then I took a few quiet breaths.", "1d ago"),
            MoodWallView.Post("sample-6", "Excited", "I finally understand the project idea and feel ready to start.", "1d ago"),
            MoodWallView.Post("sample-7", "Tired", "I am exhausted, but I still showed up and did one small thing.", "2d ago"),
            MoodWallView.Post("sample-8", "Neutral", "Nothing major happened today, and that was actually okay.", "2d ago"),
            MoodWallView.Post("sample-9", "Angry", "I got frustrated, stepped away, and came back calmer.", "3d ago"),
            MoodWallView.Post("sample-10", "Happy", "Something finally went right today, even if it was small.", "3d ago"),
            MoodWallView.Post("sample-11", "Calm", "I took a quiet moment between tasks and felt more settled.", "4d ago"),
            MoodWallView.Post("sample-12", "Stressed", "There is a lot due this week, but I made a simple plan.", "4d ago"),
        )
        return samples
    }

    private fun dayOfYear(): Int = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    private fun repeat(text: String, count: Int): String = buildString { repeat(count) { append(text) } }
}
