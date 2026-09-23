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
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class ReflectionView(
    private val activity: Activity,
    initialText: String,
    private val actions: Actions,
) : LinearLayout(activity) {
    private val emotions = listOf("Happy", "Calm", "Excited", "Sad", "Angry", "Anxious", "Stressed", "Tired", "Neutral")
    private var shareAnonymously: Boolean? = null
    private var selectedEmotion: String? = null
    private lateinit var emotionGrid: GridLayout
    private lateinit var reflectionInput: EditText
    private lateinit var countLabel: TextView
    private lateinit var privateButton: TextView
    private lateinit var shareButton: TextView

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

        content.addView(header(), LayoutParams(-1, dp(34)))
        content.addView(sectionLabel("YOUR REFLECTION"), margins(LayoutParams(-1, -2), 0, 18, 0, 9))

        val inputBox = FrameLayout(activity).apply {
            background = noteBackground()
        }
        reflectionInput = EditText(activity).apply {
            setText(initialText)
            hint = "Write about what has been on your mind..."
            textSize = 18f
            gravity = Gravity.TOP or Gravity.START
            minLines = 6
            maxLines = 6
            setSingleLine(false)
            setTextColor(0xFF4C457B.toInt())
            setHintTextColor(0xFFB2A9C8.toInt())
            background = null
            setPadding(dp(18), dp(14), dp(42), dp(8))
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = updateCount()
                override fun afterTextChanged(s: Editable?) = Unit
            })
        }
        inputBox.addView(reflectionInput, FrameLayout.LayoutParams(-1, dp(146)))
        inputBox.addView(TextView(activity).apply {
            text = "▣"
            gravity = Gravity.CENTER
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(0xFFFFFFFF.toInt())
            background = solid(0xFF31C65E.toInt(), 10)
        }, FrameLayout.LayoutParams(dp(22), dp(22), Gravity.END or Gravity.BOTTOM).apply {
            setMargins(0, 0, dp(10), dp(9))
        })
        content.addView(inputBox, LayoutParams(-1, dp(146)))

        countLabel = TextView(activity).apply {
            textSize = 15f
            gravity = Gravity.END
            setTextColor(0xFFAAA1BE.toInt())
        }
        content.addView(countLabel, margins(LayoutParams(-1, -2), 0, 5, 2, 15))
        updateCount()

        content.addView(sectionLabel("EMOTION"), margins(LayoutParams(-1, -2), 0, 0, 0, 9))
        emotionGrid = emotionGrid()
        content.addView(emotionGrid)

        content.addView(sectionLabel("VISIBILITY"), margins(LayoutParams(-1, -2), 0, 0, 0, 9))
        content.addView(visibilityRow())
        content.addView(TextView(activity).apply {
            text = "Your name and username will never appear on the Mood Wall."
            textSize = 16f
            setLineSpacing(dp(2).toFloat(), 1.0f)
            setTextColor(0xFF746A98.toInt())
        }, margins(LayoutParams(-1, -2), 0, 9, 0, 20))

        content.addView(TextView(activity).apply {
            text = "Save reflection · +15 pts"
            textSize = 19f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(0xFFFFFFFF.toInt())
            background = gradient(0xFF7B58B7.toInt(), 0xFF57A8DE.toInt(), 12)
            elevation = dp(3).toFloat()
            setOnClickListener { actions.saveReflection(reflectionInput.text.toString(), selectedEmotion, shareAnonymously) }
        }, LayoutParams(-1, dp(48)))

        addView(bottomNav(), LayoutParams(-1, dp(72)))
    }

    private fun header(): LinearLayout {
        return LinearLayout(activity).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            addView(BackIconView(activity).apply {
                setOnClickListener { actions.back() }
            }, LayoutParams(dp(34), -1))
            addView(TextView(activity).apply {
                text = "New reflection"
                textSize = 28f
                typeface = Typeface.DEFAULT_BOLD
                includeFontPadding = false
                setTextColor(0xFF151238.toInt())
            }, margins(LayoutParams(0, -1, 1f), 3, 0, 0, 0))
        }
    }

    private fun visibilityRow(): LinearLayout {
        return LinearLayout(activity).apply {
            orientation = HORIZONTAL
            privateButton = visibilityButton("🔒 Private", true)
            shareButton = visibilityButton("🌐 Share anonymously", false)
            addView(privateButton, margins(LayoutParams(0, dp(50), 1f), 0, 0, 5, 0))
            addView(shareButton, margins(LayoutParams(0, dp(50), 1f), 5, 0, 0, 0))
            refreshVisibility()
        }
    }

    private fun emotionGrid(): GridLayout {
        return GridLayout(activity).apply {
            columnCount = 3
            emotions.forEachIndexed { index, emotion ->
                addView(emotionButton(emotion), emotionCellParams(index))
            }
        }
    }

    private fun rebuildEmotionGrid() {
        emotionGrid.removeAllViews()
        emotions.forEachIndexed { index, emotion ->
            emotionGrid.addView(emotionButton(emotion), emotionCellParams(index))
        }
    }

    private fun emotionButton(emotion: String): TextView {
        val selected = emotion == selectedEmotion
        return TextView(activity).apply {
            text = "${moodIcon(emotion)} $emotion"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(if (selected) 0xFFFFFFFF.toInt() else 0xFF7A58B7.toInt())
            background = if (selected) solid(0xFF7368F0.toInt(), 13) else solid(0xFFFFFFFF.toInt(), 13)
            elevation = if (selected) dp(2).toFloat() else 0f
            setOnClickListener {
                selectedEmotion = emotion
                rebuildEmotionGrid()
            }
        }
    }

    private fun emotionCellParams(index: Int): GridLayout.LayoutParams {
        val col = index % 3
        return GridLayout.LayoutParams().apply {
            width = 0
            height = dp(42)
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(
                if (col == 0) 0 else dp(4),
                if (index < 3) 0 else dp(6),
                if (col == 2) 0 else dp(4),
                if (index >= 6) dp(15) else dp(0),
            )
        }
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

    private fun visibilityButton(label: String, privateChoice: Boolean): TextView {
        return TextView(activity).apply {
            text = label
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setOnClickListener {
                shareAnonymously = !privateChoice
                refreshVisibility()
            }
        }
    }

    private fun refreshVisibility() {
        if (!::privateButton.isInitialized || !::shareButton.isInitialized) return
        styleVisibility(privateButton, shareAnonymously == false)
        styleVisibility(shareButton, shareAnonymously == true)
    }

    private fun styleVisibility(view: TextView, selected: Boolean) {
        view.setTextColor(if (selected) 0xFF7A58B7.toInt() else 0xFF8C84AD.toInt())
        view.background = GradientDrawable().apply {
            setColor(0xFFFFFFFF.toInt())
            cornerRadius = dp(13).toFloat()
            setStroke(dp(if (selected) 2 else 1), if (selected) 0xFF8C84FF.toInt() else 0xFFEAE3F2.toInt())
        }
        view.elevation = if (selected) dp(1).toFloat() else 0f
    }

    private fun bottomNav(): LinearLayout {
        val items = listOf(
            NavItem("home", "Home") { actions.home() },
            NavItem("log", "Log") { actions.log() },
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
                val active = item.label == "Journal"
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
        if (::countLabel.isInitialized) countLabel.text = "${reflectionInput.text.length}/1000"
    }

    private fun sectionLabel(label: String): TextView = TextView(activity).apply {
        text = label
        textSize = 16f
        typeface = Typeface.DEFAULT_BOLD
        letterSpacing = 0.08f
        setTextColor(0xFFA69DBD.toInt())
    }

    private fun noteBackground(): GradientDrawable = GradientDrawable().apply {
        setColor(0xFFFFFFFF.toInt())
        cornerRadius = dp(13).toFloat()
        setStroke(dp(1), 0xFFE1DAEA.toInt())
    }

    private fun gradient(start: Int, end: Int, radius: Int): GradientDrawable =
        GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(start, end)).apply {
            cornerRadius = dp(radius).toFloat()
        }

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
        fun saveReflection(text: String, emotion: String?, shareAnonymously: Boolean?)
        fun back()
        fun home()
        fun log()
        fun journal()
        fun wall()
        fun me()
    }
}
