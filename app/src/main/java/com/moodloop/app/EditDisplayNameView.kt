package com.moodloop.app

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class EditDisplayNameView(
    private val activity: Activity,
    currentName: String,
    private val actions: Actions,
) : LinearLayout(activity) {
    private lateinit var nameInput: EditText
    private lateinit var countLabel: TextView

    init {
        orientation = VERTICAL
        setBackgroundColor(0xFFF8F1FF.toInt())
        val scroll = ScrollView(activity).apply { setBackgroundColor(0xFFF8F1FF.toInt()) }
        addView(scroll, LayoutParams(-1, 0, 1f))
        val content = vertical().apply { setPadding(dp(14), dp(30), dp(14), dp(18)) }
        scroll.addView(content)

        content.addView(header(), LayoutParams(-1, dp(34)))
        content.addView(hero(), margins(LayoutParams(-1, -2), 0, 16, 0, 0))
        content.addView(sectionLabel("DISPLAY NAME"), margins(LayoutParams(-1, -2), 0, 18, 0, 9))

        nameInput = EditText(activity).apply {
            setText(currentName)
            hint = "Enter your display name"
            textSize = 18f
            setSingleLine(true)
            setTextColor(0xFF322C5E.toInt())
            setHintTextColor(0xFFB2A9C8.toInt())
            background = fieldBackground()
            setPadding(dp(16), 0, dp(16), 0)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = updateCount()
                override fun afterTextChanged(s: Editable?) = Unit
            })
        }
        content.addView(nameInput, LayoutParams(-1, dp(58)))
        countLabel = TextView(activity).apply {
            textSize = 13f
            gravity = Gravity.END
            setTextColor(0xFF9D94B8.toInt())
        }
        content.addView(countLabel, margins(LayoutParams(-1, -2), 0, 6, 2, 18))
        updateCount()

        content.addView(TextView(activity).apply {
            text = "Save display name"
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(0xFFFFFFFF.toInt())
            background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(0xFF7B58B7.toInt(), 0xFF57A8DE.toInt())).apply {
                cornerRadius = dp(12).toFloat()
            }
            elevation = dp(3).toFloat()
            setOnClickListener { actions.save(nameInput.text.toString()) }
        }, LayoutParams(-1, dp(50)))
    }

    private fun header(): LinearLayout = LinearLayout(activity).apply {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        addView(BackIconView(activity).apply {
            setOnClickListener { actions.back() }
        }, LayoutParams(dp(34), -1))
        addView(TextView(activity).apply {
            text = "Edit display name"
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            includeFontPadding = false
            setTextColor(0xFF151238.toInt())
        }, margins(LayoutParams(0, -1, 1f), 3, 0, 0, 0))
    }

    private fun hero(): LinearLayout = vertical().apply {
        background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(0xFFDDEEEB.toInt(), 0xFFD6C2F3.toInt())).apply {
            cornerRadius = dp(16).toFloat()
        }
        elevation = dp(3).toFloat()
        setPadding(dp(18), dp(16), dp(18), dp(16))
        addView(TextView(activity).apply { text = "👤"; textSize = 30f })
        addView(TextView(activity).apply {
            text = "Choose how MoodLoop greets you"
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(0xFF322C5E.toInt())
        })
        addView(TextView(activity).apply {
            text = "This name is only used inside your private app experience."
            textSize = 14f
            setTextColor(0xFF746A98.toInt())
        }, margins(LayoutParams(-1, -2), 0, 5, 0, 0))
    }

    private fun updateCount() {
        if (::countLabel.isInitialized) countLabel.text = "${nameInput.text.length}/30"
    }

    private fun sectionLabel(value: String): TextView = TextView(activity).apply {
        text = value
        textSize = 14f
        typeface = Typeface.DEFAULT_BOLD
        letterSpacing = 0.08f
        setTextColor(0xFFA69DBD.toInt())
    }

    private fun fieldBackground(): GradientDrawable = GradientDrawable().apply {
        setColor(0xFFFFFFFF.toInt())
        cornerRadius = dp(13).toFloat()
        setStroke(dp(1), 0xFFE1DAEA.toInt())
    }

    private fun vertical(): LinearLayout = LinearLayout(activity).apply { orientation = VERTICAL }
    private fun margins(params: LayoutParams, left: Int, top: Int, right: Int, bottom: Int): LayoutParams {
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom))
        return params
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
        fun save(name: String)
        fun back()
    }
}
