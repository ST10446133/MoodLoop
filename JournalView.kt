package com.moodloop.app

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class JournalView(
    private val activity: Activity,
    private val state: State,
    private val actions: Actions,
) : LinearLayout(activity) {
    init {
        orientation = VERTICAL
        setBackgroundColor(0xFFF8F1FF.toInt())

        val scroll = ScrollView(activity).apply {
            isFillViewport = true
            setBackgroundColor(0xFFF8F1FF.toInt())
        }
        addView(scroll, LayoutParams(-1, 0, 1f))

        val content = FrameLayout(activity).apply {
            setPadding(dp(14), dp(30), dp(14), dp(16))
        }
        scroll.addView(content, FrameLayout.LayoutParams(-1, -1))

        content.addView(header(), FrameLayout.LayoutParams(-1, -2, Gravity.TOP))
        if (state.reflections.isEmpty()) {
            content.addView(emptyState(), FrameLayout.LayoutParams(-1, -2, Gravity.CENTER))
        } else {
            content.addView(reflectionList(), FrameLayout.LayoutParams(-1, -2, Gravity.TOP).apply {
                topMargin = dp(82)
            })
        }

        addView(bottomNav(), LayoutParams(-1, dp(72)))
    }

    private fun header(): FrameLayout {
        return FrameLayout(activity).apply {
            addView(vertical().apply {
                addView(TextView(activity).apply {
                    text = "Journal"
                    textSize = 31f
                    typeface = Typeface.DEFAULT_BOLD
                    includeFontPadding = false
                    setTextColor(0xFF151238.toInt())
                })
                addView(TextView(activity).apply {
                    text = "${state.reflections.size} reflections"
                    textSize = 16f
                    setTextColor(0xFF8D84B0.toInt())
                }, margins(LayoutParams(-1, -2), 0, 5, 0, 0))
            }, FrameLayout.LayoutParams(-2, -2, Gravity.START or Gravity.TOP))

            addView(TextView(activity).apply {
                text = "✏ New"
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                setTextColor(0xFFFFFFFF.toInt())
                background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(0xFF7B58B7.toInt(), 0xFF57A8DE.toInt())).apply {
                    cornerRadius = dp(18).toFloat()
                }
                elevation = dp(3).toFloat()
                setOnClickListener { actions.newReflection() }
            }, FrameLayout.LayoutParams(dp(74), dp(38), Gravity.END or Gravity.TOP))
        }
    }

    private fun emptyState(): LinearLayout {
        return vertical().apply {
            gravity = Gravity.CENTER_HORIZONTAL
            addView(BookIconView(activity), LayoutParams(dp(58), dp(58)))
            addView(TextView(activity).apply {
                text = "No reflections yet"
                textSize = 20f
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                setTextColor(0xFF4A4478.toInt())
            }, margins(LayoutParams(-1, -2), 0, 12, 0, 0))
            addView(TextView(activity).apply {
                text = "Writing about how you feel can be powerful. Your\nreflections are private unless you choose to share."
                textSize = 16f
                gravity = Gravity.CENTER
                setLineSpacing(dp(2).toFloat(), 1.0f)
                setTextColor(0xFF9D94B8.toInt())
            }, margins(LayoutParams(-1, -2), 0, 14, 0, 0))
            addView(TextView(activity).apply {
                text = "Write your first reflection"
                textSize = 17f
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                setTextColor(0xFFFFFFFF.toInt())
                background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(0xFF7B58B7.toInt(), 0xFF57A8DE.toInt())).apply {
                    cornerRadius = dp(17).toFloat()
                }
                elevation = dp(3).toFloat()
                setOnClickListener { actions.newReflection() }
            }, margins(LayoutParams(dp(206), dp(44)), 0, 19, 0, 0))
        }
    }

    private fun reflectionList(): LinearLayout {
        return vertical().apply {
            state.reflections.forEach { reflection ->
                addView(reflectionCard(reflection), margins(LayoutParams(-1, -2), 0, 0, 0, 12))
            }
        }
    }

    private fun reflectionCard(reflection: Reflection): LinearLayout {
        return vertical().apply {
            background = GradientDrawable().apply {
                setColor(0xFFFFFFFF.toInt())
                cornerRadius = dp(14).toFloat()
            }
            elevation = dp(2).toFloat()
            setPadding(dp(16), dp(14), dp(16), dp(14))
            addView(LinearLayout(activity).apply {
                orientation = HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                addView(TextView(activity).apply {
                    text = "${moodIcon(reflection.emotion)} ${reflection.emotion}"
                    textSize = 14f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(0xFF7A58B7.toInt())
                }, LayoutParams(0, -2, 1f))
                addView(TextView(activity).apply {
                    text = reflection.sharing
                    textSize = 12f
                    typeface = Typeface.DEFAULT_BOLD
                    gravity = Gravity.CENTER
                    setTextColor(0xFF8E84AF.toInt())
                    background = GradientDrawable().apply {
                        setColor(0xFFF4F1FF.toInt())
                        cornerRadius = dp(12).toFloat()
                    }
                }, LayoutParams(dp(128), dp(26)))
            })
            addView(TextView(activity).apply {
                text = reflection.text
                textSize = 17f
                setTextColor(0xFF4A4478.toInt())
            }, margins(LayoutParams(-1, -2), 0, 12, 0, 8))
            addView(TextView(activity).apply {
                text = reflection.timeText
                textSize = 13f
                setTextColor(0xFF9D94B8.toInt())
            })
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

    private fun bottomNav(): LinearLayout {
        val items = listOf(
            NavItem("home", "Home") { actions.home() },
            NavItem("log", "Log") { actions.log() },
            NavItem("journal", "Journal") { },
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

    private inner class BookIconView(activity: Activity) : View(activity) {
        private val coverPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF6F4FA8.toInt()
            style = Paint.Style.FILL
        }
        private val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF4F3B7F.toInt()
            style = Paint.Style.FILL
        }
        private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFC4B9DA.toInt()
            style = Paint.Style.STROKE
            strokeWidth = dp(2).toFloat()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val cx = width / 2f
            canvas.drawRoundRect(cx - dp(13), dp(6).toFloat(), cx + dp(13), height - dp(6).toFloat(), dp(2).toFloat(), dp(2).toFloat(), coverPaint)
            canvas.drawRect(cx - dp(13), dp(6).toFloat(), cx - dp(8), height - dp(6).toFloat(), edgePaint)
            canvas.drawRect(cx - dp(2), dp(12).toFloat(), cx + dp(8), dp(16).toFloat(), linePaint)
            canvas.drawLine(cx - dp(17), height - dp(5).toFloat(), cx + dp(17), height - dp(5).toFloat(), linePaint)
        }
    }

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

    private fun vertical(): LinearLayout = LinearLayout(activity).apply { orientation = VERTICAL }

    private fun margins(params: LayoutParams, left: Int, top: Int, right: Int, bottom: Int): LayoutParams {
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom))
        return params
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    data class State(val reflections: List<Reflection>)

    data class Reflection(
        val text: String,
        val emotion: String,
        val sharing: String,
        val timeText: String,
    )

    interface Actions {
        fun newReflection()
        fun home()
        fun log()
        fun wall()
        fun me()
    }

    private data class NavItem(val icon: String, val label: String, val action: () -> Unit)
}
