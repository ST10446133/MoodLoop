package com.moodloop.app

import android.app.Activity
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class SettingsView(private val activity: Activity, private val actions: Actions) : LinearLayout(activity) {
    init {
        orientation = VERTICAL
        setBackgroundColor(0xFFF8F1FF.toInt())
        val scroll = ScrollView(activity)
        addView(scroll, LayoutParams(-1, 0, 1f))
        val content = vertical().apply { setPadding(dp(14), dp(30), dp(14), dp(18)) }
        scroll.addView(content)
        content.addView(title("Settings"))
        content.addView(tabs(), LayoutParams(-1, dp(48)))
        content.addView(hero(), margins(LayoutParams(-1, -2), 0, 16, 0, 0))
        content.addView(sectionLabel("ACCOUNT"), margins(LayoutParams(-1, -2), 0, 16, 0, 0))
        content.addView(setting("👤", "Edit display name", "Change how your name appears in the app.") { actions.editDisplayName() }, margins(LayoutParams(-1, dp(72)), 0, 10, 0, 0))
        content.addView(setting("🔐", "Change password", "Update your Firebase sign-in password.") { actions.changePassword() }, margins(LayoutParams(-1, dp(72)), 0, 10, 0, 0))
        content.addView(sectionLabel("PREFERENCES"), margins(LayoutParams(-1, -2), 0, 16, 0, 0))
        content.addView(setting("🔔", "Reminder settings", "Choose when MoodLoop should remind you.") { actions.reminders() }, margins(LayoutParams(-1, dp(72)), 0, 10, 0, 0))
        content.addView(setting("🚪", "Log out", "Leave this account on this device.") { actions.logout() }, margins(LayoutParams(-1, dp(72)), 0, 10, 0, 0))
        content.addView(setting("🗑", "Delete account", "Permanently remove this account from MoodLoop.") { actions.deleteAccount() }, margins(LayoutParams(-1, dp(72)), 0, 10, 0, 0))
        addView(ProfileMiniNav(activity, "Me", actions, ::dp), LayoutParams(-1, dp(72)))
    }

    private fun title(value: String): TextView = TextView(activity).apply { text = value; textSize = 31f; typeface = Typeface.DEFAULT_BOLD; setTextColor(0xFF151238.toInt()) }
    private fun hero(): LinearLayout = vertical().apply {
        background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(0xFFDDEEEB.toInt(), 0xFFD6C2F3.toInt())).apply { cornerRadius = dp(16).toFloat() }
        elevation = dp(3).toFloat()
        setPadding(dp(18), dp(16), dp(18), dp(16))
        addView(TextView(activity).apply { text = "⚙️"; textSize = 30f })
        addView(TextView(activity).apply { text = "Manage your MoodLoop"; textSize = 18f; typeface = Typeface.DEFAULT_BOLD; setTextColor(0xFF322C5E.toInt()) })
        addView(TextView(activity).apply { text = "Account, password, and reminder controls live here."; textSize = 14f; setTextColor(0xFF746A98.toInt()) }, margins(LayoutParams(-1, -2), 0, 5, 0, 0))
    }
    private fun setting(icon: String, label: String, detail: String, action: () -> Unit): LinearLayout = LinearLayout(activity).apply {
        orientation = HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(dp(14), 0, dp(14), 0)
        background = solid(0xFFFFFFFF.toInt(), 14); elevation = dp(2).toFloat(); setOnClickListener { action() }
        addView(TextView(activity).apply { text = icon; textSize = 24f; gravity = Gravity.CENTER }, LayoutParams(dp(42), -1))
        addView(vertical().apply {
            addView(TextView(activity).apply { text = label; textSize = 17f; typeface = Typeface.DEFAULT_BOLD; setTextColor(0xFF322C5E.toInt()) })
            addView(TextView(activity).apply { text = detail; textSize = 13f; setTextColor(0xFF8E84AF.toInt()) }, margins(LayoutParams(-1, -2), 0, 3, 0, 0))
        }, LayoutParams(0, -2, 1f))
    }
    private fun sectionLabel(value: String): TextView = TextView(activity).apply { text = value; textSize = 14f; typeface = Typeface.DEFAULT_BOLD; letterSpacing = 0.08f; setTextColor(0xFFA69DBD.toInt()) }
    private fun tabs(): LinearLayout {
        val labels = listOf("Overview", "Tracker", "Badges", "Settings")
        return LinearLayout(activity).apply {
            orientation = HORIZONTAL; setBackgroundColor(0xFFFFFFFF.toInt())
            labels.forEach { label ->
                val active = label == "Settings"
                addView(TextView(activity).apply {
                    text = label; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER
                    setTextColor(if (active) 0xFF6F61F2.toInt() else 0xFF9D94B8.toInt())
                    background = if (active) activeTabBackground() else null
                    setOnClickListener { when (label) { "Overview" -> actions.overview(); "Tracker" -> actions.tracker(); "Badges" -> actions.badges() } }
                }, LayoutParams(0, -1, 1f))
            }
        }
    }
    private fun vertical(): LinearLayout = LinearLayout(activity).apply { orientation = VERTICAL }
    private fun margins(params: LayoutParams, left: Int, top: Int, right: Int, bottom: Int): LayoutParams { params.setMargins(dp(left), dp(top), dp(right), dp(bottom)); return params }
    private fun solid(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat() }
    private fun activeTabBackground(): GradientDrawable = GradientDrawable().apply { setColor(0x00FFFFFF); setStroke(dp(2), 0xFF6F61F2.toInt()) }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    interface Actions : ProfileMiniNav.Actions {
        fun overview(); fun tracker(); fun badges(); fun editDisplayName(); fun changePassword(); fun reminders(); fun logout(); fun deleteAccount()
    }
}
