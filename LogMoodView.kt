package com.moodloop.app

import android.app.Activity
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class LogMoodView(
    private val activity: Activity,
    initialEmotion: String,
    initialNote: String,
    private val actions: Actions,
) : LinearLayout(activity) {
    private val emotions = listOf("Happy", "Calm", "Excited", "Sad", "Angry", "Anxious", "Stressed", "Tired", "Neutral")
    private var selectedEmotion = initialEmotion.ifBlank { "Stressed" }
    private lateinit var moodGrid: GridLayout
    private lateinit var noteInput: EditText
    private lateinit var countLabel: TextView

    init {
        orientation = VERTICAL
        setBackgroundColor(0xFFF8F1FF.toInt())

        val scroll = ScrollView(activity).apply {
            isFillViewport = false
            setBackgroundColor(0xFFF8F1FF.toInt())
        }
        addView(scroll, LayoutParams(-1, 0, 1f))

        val content = vertical().apply {
            setPadding(dp(14), dp(30), dp(14), dp(12))
        }
        scroll.addView(content, FrameLayout.LayoutParams(-1, -2))

        content.addView(TextView(activity).apply {
            text = "Log your mood"
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            includeFontPadding = false
            setTextColor(0xFF151238.toInt())
        })
        content.addView(TextView(activity).apply {
            text = "How are you feeling right now?\nDate and time recorded automatically."
            textSize = 16f
            setLineSpacing(dp(2).toFloat(), 1.0f)
            setTextColor(0xFF7B739E.toInt())
        }, margins(LayoutParams(-1, -2), 0, 7, 0, 13))

        content.addView(sectionLabel("CHOOSE AN EMOTION"), margins(LayoutParams(-1, -2), 0, 0, 0, 9))
        moodGrid = emotionGrid()
        content.addView(moodGrid)

        content.addView(sectionLabel("ADD A NOTE (optional)"), margins(LayoutParams(-1, -2), 0, 14, 0, 9))
        val noteBox = FrameLayout(activity).apply {
            background = noteBackground()
            setPadding(0, 0, 0, 0)
        }
        noteInput = EditText(activity).apply {
            setText(initialNote)
            hint = "Write about what made you feel this way..."
            textSize = 16f
            gravity = Gravity.TOP or Gravity.START
            minLines = 4
            maxLines = 4
            setSingleLine(false)
            setTextColor(0xFF4C457B.toInt())
            setHintTextColor(0xFFB2A9C8.toInt())
            background = null
            setPadding(dp(18), dp(14), dp(42), dp(8))
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateCount()
                }
                override fun afterTextChanged(s: Editable?) = Unit
            })
        }
        noteBox.addView(noteInput, FrameLayout.LayoutParams(-1, dp(92)))
        noteBox.addView(TextView(activity).apply {
            text = "▣"
            gravity = Gravity.CENTER
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(0xFFFFFFFF.toInt())
            background = solid(0xFF31C65E.toInt(), 10)
        }, FrameLayout.LayoutParams(dp(22), dp(22), Gravity.END or Gravity.BOTTOM).apply {
            setMargins(0, 0, dp(10), dp(9))
        })
        content.addView(noteBox, LayoutParams(-1, dp(92)))

        countLabel = TextView(activity).apply {
            textSize = 14f
            gravity = Gravity.END
            setTextColor(0xFFAAA1BE.toInt())
        }
        content.addView(countLabel, margins(LayoutParams(-1, -2), 0, 5, 2, 10))
        updateCount()

        content.addView(TextView(activity).apply {
            text = "Save mood · +10 pts"
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(0xFFFFFFFF.toInt())
            background = gradient(0xFF7B58B7.toInt(), 0xFF57A8DE.toInt(), GradientDrawable.Orientation.LEFT_RIGHT, 12)
            elevation = dp(3).toFloat()
            setOnClickListener { actions.saveMood(selectedEmotion, noteInput.text.toString()) }
        }, margins(LayoutParams(-1, dp(48)), 0, 0, 0, 20))

        content.addView(pointsPreview(), margins(LayoutParams(-1, dp(70)), 0, 0, 0, 2))

        addView(bottomNav(), LayoutParams(-1, dp(72)))
    }

    private fun emotionGrid(): GridLayout {
        return GridLayout(activity).apply {
            columnCount = 3
            emotions.forEachIndexed { index, emotion ->
                addView(emotionCard(emotion), cellParams(index))
            }
        }
    }

    private fun rebuildGrid() {
        moodGrid.removeAllViews()
        emotions.forEachIndexed { index, emotion ->
            moodGrid.addView(emotionCard(emotion), cellParams(index))
        }
    }

    private fun emotionCard(emotion: String): LinearLayout {
        val selected = emotion == selectedEmotion
        return vertical().apply {
            gravity = Gravity.CENTER
            background = if (selected) selectedMoodBackground() else cardBackground(14)
            elevation = dp(1).toFloat()
            setOnClickListener {
                selectedEmotion = emotion
                rebuildGrid()
            }
            addView(TextView(activity).apply {
                text = moodIcon(emotion)
                textSize = 27f
                gravity = Gravity.CENTER
                includeFontPadding = false
            })
            addView(TextView(activity).apply {
                text = emotion
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                includeFontPadding = false
                setTextColor(if (selected) 0xFFDE5591.toInt() else 0xFF4A4478.toInt())
            }, margins(LayoutParams(-1, -2), 0, 7, 0, 0))
        }
    }

    private fun cellParams(index: Int): GridLayout.LayoutParams {
        val col = index % 3
        return GridLayout.LayoutParams().apply {
            width = 0
            height = dp(62)
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(
                if (col == 0) 0 else dp(4),
                if (index < 3) 0 else dp(6),
                if (col == 2) 0 else dp(4),
                0,
            )
        }
    }

    private fun pointsPreview(): LinearLayout {
        return vertical().apply {
            background = solid(0xFFF0F4FF.toInt(), 0)
            setPadding(dp(14), dp(10), dp(14), dp(10))
            addView(TextView(activity).apply {
                text = "Points you earn"
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(0xFF8D83BF.toInt())
            })
            addView(LinearLayout(activity).apply {
                orientation = HORIZONTAL
                addView(TextView(activity).apply {
                    text = "Log a mood"
                    textSize = 14f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(0xFF7A58B7.toInt())
                }, LayoutParams(0, -2, 1f))
                addView(TextView(activity).apply {
                    text = "+10 pts"
                    textSize = 14f
                    typeface = Typeface.DEFAULT_BOLD
                    gravity = Gravity.END
                    setTextColor(0xFF7A58B7.toInt())
                })
            }, margins(LayoutParams(-1, -2), 0, 8, 0, 0))
        }
    }

    private fun bottomNav(): LinearLayout {
        val items = listOf(
            NavItem("home", "Home") { actions.home() },
            NavItem("log", "Log") { },
            NavItem("journal", "Journal") { actions.journal() },
            NavItem("wall", "Wall") { actions.wall() },
            NavItem("me", "Me") { actions.me() },
        )
        return LinearLayout(activity).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(4), dp(3), dp(4), dp(12))
            setBackgroundColor(0xFFFFFFFF.toInt())
            items.forEach { item ->
                val active = item.label == "Log"
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

    private fun updateCount() {
        if (::countLabel.isInitialized) countLabel.text = "${noteInput.text.length}/300"
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
        else -> "😐"
    }

    private fun sectionLabel(label: String): TextView = TextView(activity).apply {
        text = label
        textSize = 14f
        typeface = Typeface.DEFAULT_BOLD
        letterSpacing = 0.08f
        setTextColor(0xFFA69DBD.toInt())
    }

    private fun cardBackground(radius: Int): GradientDrawable = solid(0xFFFFFFFF.toInt(), radius)

    private fun selectedMoodBackground(): GradientDrawable = GradientDrawable().apply {
        setColor(0xFFFFE9F1.toInt())
        cornerRadius = dp(14).toFloat()
        setStroke(dp(2), 0xFF8C84FF.toInt())
    }

    private fun noteBackground(): GradientDrawable = GradientDrawable().apply {
        setColor(0xFFFFFFFF.toInt())
        cornerRadius = dp(13).toFloat()
        setStroke(dp(2), 0xFF9DAEFF.toInt())
    }

    private fun gradient(start: Int, end: Int, orientation: GradientDrawable.Orientation, radius: Int): GradientDrawable =
        GradientDrawable(orientation, intArrayOf(start, end)).apply { cornerRadius = dp(radius).toFloat() }

    private fun solid(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun vertical(): LinearLayout = LinearLayout(activity).apply { orientation = VERTICAL }

    private fun margins(params: LayoutParams, left: Int, top: Int, right: Int, bottom: Int): LayoutParams {
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom))
        return params
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private data class NavItem(val icon: String, val label: String, val action: () -> Unit)

    private inner class NavIconView(
        activity: Activity,
        private val icon: String,
        private val active: Boolean,
    ) : View(activity) {
        private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (active) 0xFF7A58B7.toInt() else 0xFFA9C4D8.toInt()
            style = Paint.Style.STROKE
            strokeWidth = dp(2).toFloat()
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (active) 0xFF7A58B7.toInt() else 0xFFA9C4D8.toInt()
            style = Paint.Style.FILL
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val cx = width / 2f
            val cy = height / 2f
            when (icon) {
                "home" -> {
                    val roof = android.graphics.Path().apply {
                        moveTo(cx - dp(8), cy - dp(2))
                        lineTo(cx, cy - dp(9))
                        lineTo(cx + dp(8), cy - dp(2))
                    }
                    canvas.drawPath(roof, iconPaint)
                    canvas.drawRect(cx - dp(6), cy - dp(2), cx + dp(6), cy + dp(9), if (active) fillPaint else iconPaint)
                }
                "log" -> {
                    canvas.drawCircle(cx, cy, dp(7).toFloat(), iconPaint)
                    canvas.drawLine(cx - dp(4), cy, cx + dp(4), cy, iconPaint)
                    canvas.drawLine(cx, cy - dp(4), cx, cy + dp(4), iconPaint)
                }
                "journal" -> {
                    canvas.drawRoundRect(cx - dp(7), cy - dp(10), cx + dp(7), cy + dp(10), dp(2).toFloat(), dp(2).toFloat(), iconPaint)
                    canvas.drawLine(cx - dp(3), cy - dp(8), cx - dp(3), cy + dp(8), iconPaint)
                }
                "wall" -> {
                    canvas.drawRoundRect(cx - dp(9), cy - dp(7), cx + dp(9), cy + dp(6), dp(2).toFloat(), dp(2).toFloat(), iconPaint)
                    canvas.drawLine(cx - dp(5), cy - dp(2), cx + dp(5), cy - dp(2), iconPaint)
                    canvas.drawLine(cx - dp(5), cy + dp(2), cx + dp(2), cy + dp(2), iconPaint)
                    canvas.drawLine(cx - dp(3), cy + dp(6), cx - dp(6), cy + dp(10), iconPaint)
                }
                "me" -> {
                    canvas.drawCircle(cx, cy - dp(6), dp(4).toFloat(), iconPaint)
                    canvas.drawRoundRect(cx - dp(8), cy, cx + dp(8), cy + dp(10), dp(6).toFloat(), dp(6).toFloat(), iconPaint)
                }
            }
        }
    }

    interface Actions {
        fun saveMood(emotion: String, note: String)
        fun home()
        fun journal()
        fun wall()
        fun me()
    }
}
