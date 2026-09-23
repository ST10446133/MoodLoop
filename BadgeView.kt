package com.moodloop.app

import android.app.Activity
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class BadgeView(private val activity: Activity, private val state: State, private val actions: Actions) : LinearLayout(activity) {
    init {
        orientation = VERTICAL
        setBackgroundColor(0xFFF8F1FF.toInt())
        val scroll = ScrollView(activity)
        addView(scroll, LayoutParams(-1, 0, 1f))
        val content = vertical().apply { setPadding(dp(14), dp(30), dp(14), dp(18)) }
        scroll.addView(content)
        content.addView(title("Badges"))
        content.addView(tabs(), LayoutParams(-1, dp(48)))
        content.addView(hero(), margins(LayoutParams(-1, -2), 0, 16, 0, 0))
        content.addView(sectionLabel("EARNED BADGES"), margins(LayoutParams(-1, -2), 0, 16, 0, 0))
        if (state.badges.isEmpty()) {
            content.addView(card("🏅", "No badges yet", "Your badges will appear after moods, reflections, streaks, and sharing."), margins(LayoutParams(-1, -2), 0, 10, 0, 0))
        } else {
            state.badges.forEach { badge -> content.addView(card("🏅", badge.name, "Earned ${badge.date}"), margins(LayoutParams(-1, -2), 0, 10, 0, 0)) }
        }
        addView(ProfileMiniNav(activity, "Me", actions, ::dp), LayoutParams(-1, dp(72)))
    }

    private fun title(value: String): TextView = TextView(activity).apply { text = value; textSize = 31f; typeface = Typeface.DEFAULT_BOLD; setTextColor(0xFF151238.toInt()) }
    private fun hero(): LinearLayout = vertical().apply {
        background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(0xFFFFE0C6.toInt(), 0xFFD6C2F3.toInt())).apply { cornerRadius = dp(16).toFloat() }
        elevation = dp(3).toFloat()
        setPadding(dp(18), dp(16), dp(18), dp(16))
        addView(TextView(activity).apply { text = "🏅 ${state.badges.size}"; textSize = 30f; typeface = Typeface.DEFAULT_BOLD; setTextColor(0xFF322C5E.toInt()) })
        addView(TextView(activity).apply { text = "Achievements unlocked"; textSize = 17f; typeface = Typeface.DEFAULT_BOLD; setTextColor(0xFF4A3FA2.toInt()) })
        addView(TextView(activity).apply { text = "Keep checking in to collect more progress badges."; textSize = 14f; setTextColor(0xFF746A98.toInt()) }, margins(LayoutParams(-1, -2), 0, 5, 0, 0))
    }
    private fun card(icon: String, title: String, body: String): LinearLayout = vertical().apply {
        background = solid(0xFFFFFFFF.toInt(), 14); elevation = dp(2).toFloat(); setPadding(dp(16), dp(14), dp(16), dp(14))
        addView(TextView(activity).apply { text = icon; textSize = 28f })
        addView(TextView(activity).apply { text = title; textSize = 19f; typeface = Typeface.DEFAULT_BOLD; setTextColor(0xFF322C5E.toInt()) })
        addView(TextView(activity).apply { text = body; textSize = 15f; setTextColor(0xFF8E84AF.toInt()) })
    }
    private fun sectionLabel(value: String): TextView = TextView(activity).apply { text = value; textSize = 14f; typeface = Typeface.DEFAULT_BOLD; letterSpacing = 0.08f; setTextColor(0xFFA69DBD.toInt()) }
    private fun tabs(): LinearLayout = tabRow("Badges")
    private fun tabRow(activeLabel: String): LinearLayout {
        val labels = listOf("Overview", "Tracker", "Badges", "Settings")
        return LinearLayout(activity).apply {
            orientation = HORIZONTAL; setBackgroundColor(0xFFFFFFFF.toInt())
            labels.forEach { label ->
                val active = label == activeLabel
                addView(TextView(activity).apply {
                    text = label; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER
                    setTextColor(if (active) 0xFF6F61F2.toInt() else 0xFF9D94B8.toInt())
                    background = if (active) activeTabBackground() else null
                    setOnClickListener { when (label) { "Overview" -> actions.overview(); "Tracker" -> actions.tracker(); "Settings" -> actions.settings() } }
                }, LayoutParams(0, -1, 1f))
            }
        }
    }
    private fun vertical(): LinearLayout = LinearLayout(activity).apply { orientation = VERTICAL }
    private fun margins(params: LayoutParams, left: Int, top: Int, right: Int, bottom: Int): LayoutParams { params.setMargins(dp(left), dp(top), dp(right), dp(bottom)); return params }
    private fun solid(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat() }
    private fun activeTabBackground(): GradientDrawable = GradientDrawable().apply { setColor(0x00FFFFFF); setStroke(dp(2), 0xFF6F61F2.toInt()) }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    data class State(val badges: List<Badge>)
    data class Badge(val name: String, val date: String)
    interface Actions : ProfileMiniNav.Actions { fun overview(); fun tracker(); fun settings() }
}
