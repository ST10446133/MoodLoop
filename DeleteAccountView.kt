package com.moodloop.app

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class DeleteAccountView(
    private val activity: Activity,
    private val actions: Actions,
) : LinearLayout(activity) {
    init {
        orientation = VERTICAL
        setBackgroundColor(0xFFF8F1FF.toInt())
        val scroll = ScrollView(activity).apply { setBackgroundColor(0xFFF8F1FF.toInt()) }
        addView(scroll, LayoutParams(-1, 0, 1f))
        val content = vertical().apply { setPadding(dp(14), dp(30), dp(14), dp(18)) }
        scroll.addView(content)

        content.addView(header(), LayoutParams(-1, dp(34)))
        content.addView(hero(), margins(LayoutParams(-1, -2), 0, 16, 0, 0))
        content.addView(sectionLabel("DELETE ACCOUNT"), margins(LayoutParams(-1, -2), 0, 18, 0, 9))
        content.addView(dangerButton("Delete my account") { actions.confirmDelete() }, LayoutParams(-1, dp(50)))
        content.addView(cancelButton("Cancel") { actions.back() }, margins(LayoutParams(-1, dp(50)), 0, 10, 0, 0))
    }

    private fun header(): LinearLayout = LinearLayout(activity).apply {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        addView(BackIconView(activity).apply {
            setOnClickListener { actions.back() }
        }, LayoutParams(dp(34), -1))
        addView(TextView(activity).apply {
            text = "Delete account"
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            includeFontPadding = false
            setTextColor(0xFF151238.toInt())
        }, margins(LayoutParams(0, -1, 1f), 3, 0, 0, 0))
    }

    private fun hero(): LinearLayout = vertical().apply {
        background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(0xFFFFD8D8.toInt(), 0xFFD6C2F3.toInt())).apply {
            cornerRadius = dp(16).toFloat()
        }
        elevation = dp(3).toFloat()
        setPadding(dp(18), dp(16), dp(18), dp(16))
        addView(TextView(activity).apply { text = "🗑"; textSize = 30f })
        addView(TextView(activity).apply {
            text = "Remove your MoodLoop account"
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(0xFF322C5E.toInt())
        })
        addView(TextView(activity).apply {
            text = "This action cannot be undone once it succeeds."
            textSize = 14f
            setTextColor(0xFF746A98.toInt())
        }, margins(LayoutParams(-1, -2), 0, 5, 0, 0))
    }

    private fun dangerButton(textValue: String, action: () -> Unit): TextView = TextView(activity).apply {
        text = textValue
        textSize = 17f
        typeface = Typeface.DEFAULT_BOLD
        gravity = Gravity.CENTER
        setTextColor(0xFFFFFFFF.toInt())
        background = solid(0xFFD85261.toInt(), 12)
        elevation = dp(3).toFloat()
        setOnClickListener { action() }
    }

    private fun cancelButton(textValue: String, action: () -> Unit): TextView = TextView(activity).apply {
        text = textValue
        textSize = 17f
        typeface = Typeface.DEFAULT_BOLD
        gravity = Gravity.CENTER
        setTextColor(0xFF7A58B7.toInt())
        background = solid(0xFFFFFFFF.toInt(), 12).apply {
            setStroke(dp(1), 0xFFE1DAEA.toInt())
        }
        setOnClickListener { action() }
    }

    private fun sectionLabel(value: String): TextView = TextView(activity).apply {
        text = value
        textSize = 14f
        typeface = Typeface.DEFAULT_BOLD
        letterSpacing = 0.08f
        setTextColor(0xFFA69DBD.toInt())
    }

    private fun vertical(): LinearLayout = LinearLayout(activity).apply { orientation = VERTICAL }
    private fun margins(params: LayoutParams, left: Int, top: Int, right: Int, bottom: Int): LayoutParams {
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom))
        return params
    }
    private fun solid(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private inner class BackIconView(activity: Activity) : View(activity) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF322C5E.toInt()
            strokeWidth = dp(2).toFloat()
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            style = Paint.Style.STROKE
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val cx = width / 2f
            val cy = height / 2f
            canvas.drawLine(cx + dp(5), cy, cx - dp(5), cy, paint)
            canvas.drawLine(cx - dp(5), cy, cx - dp(1), cy - dp(4), paint)
            canvas.drawLine(cx - dp(5), cy, cx - dp(1), cy + dp(4), paint)
        }
    }

    interface Actions {
        fun confirmDelete()
        fun back()
    }
}
