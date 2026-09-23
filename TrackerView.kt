package com.moodloop.app

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView

class TrackerView(
    private val activity: Activity,
    private val state: State,
    private val actions: Actions,
) : LinearLayout(activity) {
    init {
        orientation = VERTICAL
        setBackgroundColor(0xFFF8F1FF.toInt())
        val scroll = ScrollView(activity).apply { setBackgroundColor(0xFFF8F1FF.toInt()) }
        addView(scroll, LayoutParams(-1, 0, 1f))
        val content = vertical().apply { setPadding(dp(14), dp(30), dp(14), dp(18)) }
        scroll.addView(content, LayoutParams(-1, -2))
        content.addView(title("Tracker"))
        content.addView(tabs(), LayoutParams(-1, dp(48)))
        content.addView(heroCard(), margins(LayoutParams(-1, -2), 0, 16, 0, 0))
        content.addView(sectionLabel("LAST 7 DAYS"), margins(LayoutParams(-1, -2), 0, 16, 0, 0))
        state.trends.forEach { trend ->
            content.addView(trendRow(trend), margins(LayoutParams(-1, -2), 0, 10, 0, 0))
        }
        addView(bottomNav(), LayoutParams(-1, dp(72)))
    }

    private fun trendRow(trend: TrendItem): LinearLayout = vertical().apply {
        background = solid(0xFFFFFFFF.toInt(), 14)
        elevation = dp(2).toFloat()
        setPadding(dp(16), dp(12), dp(16), dp(12))
        addView(LinearLayout(activity).apply {
            orientation = HORIZONTAL
            addView(TextView(activity).apply {
                text = trend.label
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(0xFF322C5E.toInt())
            }, LayoutParams(0, -2, 1f))
            addView(TextView(activity).apply {
                text = "${trend.count}"
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(0xFF7A58B7.toInt())
            })
        })
        addView(ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = state.maxTrendCount.coerceAtLeast(1)
            progress = trend.count
        }, margins(LayoutParams(-1, dp(8)), 0, 10, 0, 0))
    }

    private fun title(value: String): TextView = TextView(activity).apply {
        text = value
        textSize = 31f
        typeface = Typeface.DEFAULT_BOLD
        setTextColor(0xFF151238.toInt())
    }

    private fun tabs(): LinearLayout = tabRow("Tracker")

    private fun heroCard(): LinearLayout = vertical().apply {
        background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(0xFF7B58B7.toInt(), 0xFF57A8DE.toInt())).apply {
            cornerRadius = dp(16).toFloat()
        }
        elevation = dp(3).toFloat()
        setPadding(dp(18), dp(16), dp(18), dp(16))
        addView(TextView(activity).apply {
            text = "📊 ${state.totalEntries}"
            textSize = 30f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(0xFFFFFFFF.toInt())
        })
        addView(TextView(activity).apply {
            text = "Mood entries logged"
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(0xFFFFFFFF.toInt())
        }, margins(LayoutParams(-1, -2), 0, 3, 0, 0))
        addView(TextView(activity).apply {
            text = if (state.totalEntries == 0) "Your trend will appear after you log moods." else "Your recent check-ins are shown below."
            textSize = 14f
            setTextColor(0xEFFFFFFF.toInt())
        }, margins(LayoutParams(-1, -2), 0, 5, 0, 0))
    }

    private fun sectionLabel(value: String): TextView = TextView(activity).apply {
        text = value
        textSize = 14f
        typeface = Typeface.DEFAULT_BOLD
        letterSpacing = 0.08f
        setTextColor(0xFFA69DBD.toInt())
    }

    private fun tabRow(activeLabel: String): LinearLayout {
        val labels = listOf("Overview", "Tracker", "Badges", "Settings")
        return LinearLayout(activity).apply {
            orientation = HORIZONTAL
            setBackgroundColor(0xFFFFFFFF.toInt())
            labels.forEach { label ->
                val active = label == activeLabel
                addView(TextView(activity).apply {
                    text = label
                    textSize = 13f
                    typeface = Typeface.DEFAULT_BOLD
                    gravity = Gravity.CENTER
                    setTextColor(if (active) 0xFF6F61F2.toInt() else 0xFF9D94B8.toInt())
                    background = if (active) activeTabBackground() else null
                    setOnClickListener {
                        when (label) {
                            "Overview" -> actions.overview()
                            "Badges" -> actions.badges()
                            "Settings" -> actions.settings()
                        }
                    }
                }, LayoutParams(0, -1, 1f))
            }
        }
    }

    private fun bottomNav(): LinearLayout = nav("Me", actions)
    private fun vertical(): LinearLayout = LinearLayout(activity).apply { orientation = VERTICAL }
    private fun margins(params: LayoutParams, left: Int, top: Int, right: Int, bottom: Int): LayoutParams { params.setMargins(dp(left), dp(top), dp(right), dp(bottom)); return params }
    private fun solid(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat() }
    private fun activeTabBackground(): GradientDrawable = GradientDrawable().apply { setColor(0x00FFFFFF); setStroke(dp(2), 0xFF6F61F2.toInt()) }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun nav(activeLabel: String, actions: Actions): LinearLayout {
        val items = listOf(
            NavItem("home", "Home") { actions.home() },
            NavItem("log", "Log") { actions.log() },
            NavItem("journal", "Journal") { actions.journal() },
            NavItem("wall", "Wall") { actions.wall() },
            NavItem("me", "Me") { },
        )
        return LinearLayout(activity).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(4), dp(3), dp(4), dp(12))
            setBackgroundColor(0xFFFFFFFF.toInt())
            items.forEach { item ->
                val active = item.label == activeLabel
                addView(vertical().apply {
                    gravity = Gravity.CENTER
                    setOnClickListener { item.action() }
                    addView(NavIconView(activity, item.icon, active), LayoutParams(-1, dp(27)))
                    addView(TextView(activity).apply {
                        text = item.label
                        textSize = 13f
                        typeface = Typeface.DEFAULT_BOLD
                        gravity = Gravity.CENTER
                        includeFontPadding = false
                        setTextColor(if (active) 0xFF7A58B7.toInt() else 0xFF97AFC3.toInt())
                    }, LayoutParams(-1, dp(22)))
                }, LayoutParams(0, -1, 1f))
            }
        }
    }

    data class State(val totalEntries: Int, val trends: List<TrendItem>, val maxTrendCount: Int)
    data class TrendItem(val label: String, val count: Int)

    interface Actions {
        fun overview()
        fun badges()
        fun settings()
        fun home()
        fun log()
        fun journal()
        fun wall()
    }

    private data class NavItem(val icon: String, val label: String, val action: () -> Unit)
    private inner class NavIconView(activity: Activity, private val icon: String, private val active: Boolean) : View(activity) {
        private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = if (active) 0xFF7A58B7.toInt() else 0xFFA9C4D8.toInt(); style = Paint.Style.STROKE; strokeWidth = dp(2).toFloat(); strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND }
        private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = if (active) 0xFF7A58B7.toInt() else 0xFFA9C4D8.toInt(); style = Paint.Style.FILL }
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val cx = width / 2f; val cy = height / 2f
            when (icon) {
                "home" -> { val roof = android.graphics.Path().apply { moveTo(cx - dp(8), cy - dp(2)); lineTo(cx, cy - dp(9)); lineTo(cx + dp(8), cy - dp(2)) }; canvas.drawPath(roof, iconPaint); canvas.drawRect(cx - dp(6), cy - dp(2), cx + dp(6), cy + dp(9), if (active) fillPaint else iconPaint) }
                "log" -> { canvas.drawCircle(cx, cy, dp(7).toFloat(), iconPaint); canvas.drawLine(cx - dp(4), cy, cx + dp(4), cy, iconPaint); canvas.drawLine(cx, cy - dp(4), cx, cy + dp(4), iconPaint) }
                "journal" -> { canvas.drawRoundRect(cx - dp(7), cy - dp(10), cx + dp(7), cy + dp(10), dp(2).toFloat(), dp(2).toFloat(), iconPaint); canvas.drawLine(cx - dp(3), cy - dp(8), cx - dp(3), cy + dp(8), iconPaint) }
                "wall" -> { canvas.drawRoundRect(cx - dp(9), cy - dp(7), cx + dp(9), cy + dp(6), dp(2).toFloat(), dp(2).toFloat(), iconPaint); canvas.drawLine(cx - dp(5), cy - dp(2), cx + dp(5), cy - dp(2), iconPaint); canvas.drawLine(cx - dp(5), cy + dp(2), cx + dp(2), cy + dp(2), iconPaint); canvas.drawLine(cx - dp(3), cy + dp(6), cx - dp(6), cy + dp(10), iconPaint) }
                "me" -> { canvas.drawCircle(cx, cy - dp(6), dp(4).toFloat(), iconPaint); canvas.drawRoundRect(cx - dp(8), cy, cx + dp(8), cy + dp(10), dp(6).toFloat(), dp(6).toFloat(), iconPaint) }
            }
        }
    }
}
