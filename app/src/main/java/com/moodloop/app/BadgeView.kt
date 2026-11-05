package com.moodloop.app

import android.app.Activity
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import android.widget.TextView

class BadgeView(private val activity: Activity, private val state: State, private val actions: Actions) : LinearLayout(activity) {
    init {
        orientation = VERTICAL
        setBackgroundColor(0xFFF8F1FF.toInt())
        val scroll = ScrollView(activity)
        addView(scroll, LayoutParams(-1, 0, 1f))
        val content = vertical().apply { setPadding(dp(18), dp(34), dp(18), dp(24)) }
        scroll.addView(content)
        content.addView(title(tr("Badges")))
        content.addView(TextView(activity).apply {
            text = tr("Celebrate the progress you have made.")
            textSize = 14f
            setTextColor(0xFF8E84AF.toInt())
        }, margins(LayoutParams(-1, -2), 0, 3, 0, 0))
        content.addView(tabs(), margins(LayoutParams(-1, dp(48)), 0, 13, 0, 0))
        content.addView(hero(), margins(LayoutParams(-1, -2), 0, 6, 0, 0))
        content.addView(sectionLabel(tr("EARNED BADGES")), margins(LayoutParams(-1, -2), 0, 22, 0, 0))
        if (state.badges.isEmpty()) {
            content.addView(card("🏅", "No badges yet", "Your badges will appear after moods, reflections, streaks, and sharing.", empty = true), margins(LayoutParams(-1, -2), 0, 9, 0, 0))
        } else {
            state.badges.forEach { badge -> content.addView(card("🏅", badge.name, "Earned ${badge.date}"), margins(LayoutParams(-1, -2), 0, 9, 0, 0)) }
        }
        addView(ProfileMiniNav(activity, "Me", actions, ::dp), LayoutParams(-1, dp(72)))
    }

    private fun title(value: String): TextView = TextView(activity).apply { text = value; textSize = 30f; typeface = Typeface.DEFAULT_BOLD; setTextColor(0xFF151238.toInt()) }
    private fun hero(): LinearLayout = LinearLayout(activity).apply {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(0xFFFFE9DF.toInt(), 0xFFE7DDFB.toInt())).apply { cornerRadius = dp(22).toFloat() }
        elevation = dp(3).toFloat()
        setPadding(dp(18), dp(18), dp(18), dp(18))
        addView(TextView(activity).apply {
            text = "🏅"
            textSize = 26f
            gravity = Gravity.CENTER
            background = solid(0xBFFFFFFF.toInt(), 18)
        }, LayoutParams(dp(54), dp(54)))
        addView(vertical().apply {
            addView(TextView(activity).apply { text = "${state.badges.size}"; textSize = 28f; typeface = Typeface.DEFAULT_BOLD; includeFontPadding = false; setTextColor(0xFF241C4E.toInt()) })
            addView(TextView(activity).apply { text = tr("Achievements unlocked"); textSize = 14f; typeface = Typeface.DEFAULT_BOLD; setTextColor(0xFF4A3FA2.toInt()) }, margins(LayoutParams(-1, -2), 0, 2, 0, 0))
            addView(TextView(activity).apply { text = tr("Keep checking in to collect more progress badges."); textSize = 12f; setTextColor(0xFF746A98.toInt()) }, margins(LayoutParams(-1, -2), 0, 2, 0, 0))
        }, LayoutParams(0, -2, 1f).apply { marginStart = dp(14) })
    }
    private fun card(icon: String, title: String, body: String, empty: Boolean = false): LinearLayout = LinearLayout(activity).apply {
        orientation = HORIZONTAL
        gravity = if (empty) Gravity.TOP else Gravity.CENTER_VERTICAL
        background = solid(0xFFFFFFFF.toInt(), 18)
        elevation = dp(2).toFloat()
        setPadding(dp(16), dp(15), dp(16), dp(15))
        addView(TextView(activity).apply {
            text = icon
            textSize = if (empty) 26f else 22f
            gravity = Gravity.CENTER
            background = solid(if (empty) 0xFFF1EDFF.toInt() else 0xFFFFF1E9.toInt(), 18)
        }, LayoutParams(dp(48), dp(48)))
        addView(vertical().apply {
            addView(TextView(activity).apply { text = tr(title); textSize = 16f; typeface = Typeface.DEFAULT_BOLD; setTextColor(0xFF322C5E.toInt()) })
            addView(TextView(activity).apply { text = tr(body); textSize = 13f; setTextColor(0xFF8E84AF.toInt()) }, margins(LayoutParams(-1, -2), 0, 3, 0, 0))
        }, LayoutParams(0, -2, 1f).apply { marginStart = dp(13) })
    }
    private fun sectionLabel(value: String): TextView = TextView(activity).apply { text = value; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; letterSpacing = 0.08f; setTextColor(0xFFA69DBD.toInt()) }
    private fun tabs(): HorizontalScrollView = tabRow("Badges")
    private fun tabRow(activeLabel: String): HorizontalScrollView {
        val labels = listOf("Overview", "Tracker", "Badges", "Settings")
        return HorizontalScrollView(activity).apply {
            isHorizontalScrollBarEnabled = false
            clipToPadding = false
            addView(LinearLayout(activity).apply {
            orientation = HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            labels.forEach { label ->
                val active = label == activeLabel
                addView(TextView(activity).apply {
                    text = tr(label); textSize = 13f; typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER
                    setPadding(dp(16), 0, dp(16), 0)
                    setTextColor(if (active) 0xFFFFFFFF.toInt() else 0xFF756B94.toInt())
                    background = solid(if (active) 0xFF7368F0.toInt() else 0xFFFFFFFF.toInt(), 18)
                    elevation = if (active) dp(2).toFloat() else 0f
                    setOnClickListener { when (label) { "Overview" -> actions.overview(); "Tracker" -> actions.tracker(); "Settings" -> actions.settings() } }
                }, margins(LayoutParams(-2, dp(42)), 0, 0, 8, 0))
            }
            })
        }
    }
    private fun vertical(): LinearLayout = LinearLayout(activity).apply { orientation = VERTICAL }
    private fun margins(params: LayoutParams, left: Int, top: Int, right: Int, bottom: Int): LayoutParams { params.setMargins(dp(left), dp(top), dp(right), dp(bottom)); return params }
    private fun solid(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat() }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    private fun tr(value: String): String = MoodLoopLanguage.translate(activity, value)
    data class State(val badges: List<Badge>)
    data class Badge(val name: String, val date: String)
    interface Actions : ProfileMiniNav.Actions { fun overview(); fun tracker(); fun settings() }
}
