package com.moodloop.app

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

class ProfileMiniNav(
    private val activity: Activity,
    private val activeLabel: String,
    private val actions: Actions,
    private val dpValue: (Int) -> Int,
) : LinearLayout(activity) {
    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        setPadding(dp(4), dp(3), dp(4), dp(12))
        setBackgroundColor(0xFFFFFFFF.toInt())
        listOf(
            NavItem("home", "Home") { actions.home() },
            NavItem("log", "Log") { actions.log() },
            NavItem("journal", "Journal") { actions.journal() },
            NavItem("wall", "Wall") { actions.wall() },
            NavItem("me", "Me") { },
        ).forEach { item ->
            val active = item.label == activeLabel
            addView(LinearLayout(activity).apply {
                orientation = VERTICAL
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

    private fun dp(value: Int): Int = dpValue(value)
    interface Actions { fun home(); fun log(); fun journal(); fun wall() }
    private data class NavItem(val icon: String, val label: String, val action: () -> Unit)
    private inner class NavIconView(activity: Activity, private val icon: String, private val active: Boolean) : View(activity) {
        private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = if (active) 0xFF7A58B7.toInt() else 0xFFA9C4D8.toInt(); style = Paint.Style.STROKE; strokeWidth = dp(2).toFloat(); strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND }
        private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = if (active) 0xFF7A58B7.toInt() else 0xFFA9C4D8.toInt(); style = Paint.Style.FILL }
        override fun onDraw(canvas: Canvas) {
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
