package com.moodloop.app

import android.app.Activity
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView

class HomePageView(
    private val activity: Activity,
    private val state: State,
    private val actions: Actions,
) : LinearLayout(activity) {
    init {
        orientation = VERTICAL
        setBackgroundColor(0xFFF8F1FF.toInt())

        val scroll = ScrollView(activity).apply {
            isFillViewport = false
            setBackgroundColor(0xFFF8F1FF.toInt())
        }
        addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        val content = vertical()
        scroll.addView(content, FrameLayout.LayoutParams(-1, -2))

        content.addView(HeaderView(activity), LinearLayout.LayoutParams(-1, dp(150)))
        val body = vertical().apply { setPadding(dp(14), dp(16), dp(14), dp(18)) }
        content.addView(body, LinearLayout.LayoutParams(-1, -2))

        body.addView(statsRow())
        body.addView(progressCard(), margins(LinearLayout.LayoutParams(-1, -2), 0, 14, 0, 0))
        body.addView(latestMoodCard(), margins(LinearLayout.LayoutParams(-1, -2), 0, 14, 0, 0))

        body.addView(sectionLabel("QUICK ACCESS"), margins(LinearLayout.LayoutParams(-1, -2), 0, 15, 0, 8))
        body.addView(quickAccessGrid())

        addView(bottomNav(), LayoutParams(-1, dp(72)))
    }

    private fun statsRow(): LinearLayout {
        return LinearLayout(activity).apply {
            orientation = HORIZONTAL
            addView(statCard("🔥", "${state.streakDays}d", "STREAK", "Best ${state.bestStreakDays}d"), margins(LayoutParams(0, dp(98), 1f), 0, 0, 6, 0))
            addView(statCard("⭐", state.points.toString(), "POINTS", "total"), margins(LayoutParams(0, dp(98), 1f), 6, 0, 6, 0))
            addView(statCard("🎮", state.level.toString(), "LEVEL", "${state.points} / ${state.nextLevelAt} xp"), margins(LayoutParams(0, dp(98), 1f), 6, 0, 0, 0))
        }
    }

    private fun statCard(icon: String, value: String, label: String, foot: String): LinearLayout {
        return vertical().apply {
            gravity = Gravity.CENTER
            background = cardBackground(18)
            elevation = dp(2).toFloat()
            addView(TextView(activity).apply {
                text = icon
                textSize = 23f
                gravity = Gravity.CENTER
            })
            addView(TextView(activity).apply {
                text = value
                textSize = 24f
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                setTextColor(0xFF201957.toInt())
                includeFontPadding = false
            })
            addView(TextView(activity).apply {
                text = label
                textSize = 12f
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                setTextColor(0xFF8D84B0.toInt())
            })
            addView(TextView(activity).apply {
                text = foot
                textSize = 11f
                gravity = Gravity.CENTER
                setTextColor(0xFFB3AACB.toInt())
            })
        }
    }

    private fun progressCard(): LinearLayout {
        return vertical().apply {
            background = cardBackground(16)
            elevation = dp(2).toFloat()
            setPadding(dp(14), dp(11), dp(14), dp(11))
            addView(LinearLayout(activity).apply {
                orientation = HORIZONTAL
                addView(TextView(activity).apply {
                    text = "Level ${state.level} progress"
                    textSize = 15f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(0xFF5E548B.toInt())
                }, LayoutParams(0, -2, 1f))
                addView(TextView(activity).apply {
                    text = "${state.progressPercent}%"
                    textSize = 15f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(0xFF8A80B2.toInt())
                    gravity = Gravity.END
                })
            })
            addView(ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal).apply {
                max = 100
                progress = state.progressPercent
                progressDrawable = progressDrawableForBar()
            }, margins(LayoutParams(-1, dp(9)), 0, 10, 0, 0))
            addView(TextView(activity).apply {
                text = "${state.pointsToNextLevel} pts to Level ${state.level + 1}"
                textSize = 11f
                setTextColor(0xFFB0A8C8.toInt())
            }, margins(LayoutParams(-1, -2), 0, 5, 0, 0))
        }
    }

    private fun latestMoodCard(): LinearLayout {
        return vertical().apply {
            background = cardBackground(16)
            elevation = dp(2).toFloat()
            setPadding(dp(14), dp(12), dp(14), dp(13))
            addView(sectionLabel("LATEST MOOD"))
            addView(LinearLayout(activity).apply {
                orientation = HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dp(9), 0, 0)
                addView(TextView(activity).apply {
                    text = state.latestMoodIcon
                    textSize = 28f
                    gravity = Gravity.CENTER
                }, LayoutParams(dp(42), dp(48)))
                addView(vertical().apply {
                    addView(TextView(activity).apply {
                        text = state.latestMoodTitle
                        textSize = 18f
                        typeface = Typeface.DEFAULT_BOLD
                        setTextColor(0xFF201957.toInt())
                        includeFontPadding = false
                    })
                    addView(TextView(activity).apply {
                        text = state.latestMoodNote
                        textSize = 14f
                        setTextColor(0xFF756B9E.toInt())
                    })
                    addView(TextView(activity).apply {
                        text = state.latestMoodDate
                        textSize = 13f
                        setTextColor(0xFF8E84AF.toInt())
                    })
                }, LayoutParams(0, -2, 1f))
            })
        }
    }

    private fun quickAccessGrid(): GridLayout {
        return GridLayout(activity).apply {
            columnCount = 2
            addView(quickAction("😊", "Log a mood", 0xFFECE0FA.toInt()) { actions.logMood() }, quickParams(0, 0, 6, 6))
            addView(quickAction("📓", "Write\nreflection", 0xFFDDEEEB.toInt()) { actions.writeReflection() }, quickParams(6, 0, 0, 6))
            addView(quickAction("🌐", "Mood Wall", 0xFFDDEEFF.toInt()) { actions.openMoodWall() }, quickParams(0, 6, 6, 0))
            addView(quickAction("👤", "My profile", 0xFFF2E7DE.toInt()) { actions.openProfile() }, quickParams(6, 6, 0, 0))
        }
    }

    private fun quickParams(left: Int, top: Int, right: Int, bottom: Int): GridLayout.LayoutParams {
        return GridLayout.LayoutParams().apply {
            width = 0
            height = dp(66)
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(dp(left), dp(top), dp(right), dp(bottom))
        }
    }

    private fun quickAction(icon: String, label: String, color: Int, action: () -> Unit): LinearLayout {
        return LinearLayout(activity).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(10), 0, dp(10), 0)
            background = solid(color, 14)
            elevation = dp(1).toFloat()
            setOnClickListener { action() }
            addView(TextView(activity).apply {
                text = icon
                textSize = 25f
                gravity = Gravity.CENTER
            }, LayoutParams(dp(38), -1))
            addView(TextView(activity).apply {
                text = label
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(0xFF2D275E.toInt())
                gravity = Gravity.CENTER_VERTICAL
            })
        }
    }

    private fun bottomNav(): LinearLayout {
        val items = listOf(
            NavItem("home", "Home") { actions.home() },
            NavItem("log", "Log") { actions.logMood() },
            NavItem("journal", "Journal") { actions.writeReflection() },
            NavItem("wall", "Wall") { actions.openMoodWall() },
            NavItem("me", "Me") { actions.openProfile() },
        )
        return LinearLayout(activity).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(4), dp(3), dp(4), dp(12))
            setBackgroundColor(0xFFFFFFFF.toInt())
            items.forEach { item ->
                addView(vertical().apply {
                    gravity = Gravity.CENTER
                    setOnClickListener { item.action() }
                    addView(NavIconView(activity, item.icon, item.label == "Home"), LayoutParams(-1, dp(27)))
                    addView(TextView(activity).apply {
                        text = item.label
                        textSize = 12f
                        typeface = Typeface.DEFAULT_BOLD
                        gravity = Gravity.CENTER
                        includeFontPadding = false
                        setTextColor(if (item.label == "Home") 0xFF7A58B7.toInt() else 0xFF97AFC3.toInt())
                    }, LayoutParams(-1, dp(22)))
                }, LayoutParams(0, -1, 1f))
            }
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

    private inner class HeaderView(activity: Activity) : FrameLayout(activity) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        init {
            setWillNotDraw(false)
            setPadding(dp(18), dp(18), dp(18), dp(14))
            addView(vertical().apply {
                addView(TextView(activity).apply {
                    text = "MOODLOOP"
                    textSize = 12f
                    typeface = Typeface.DEFAULT_BOLD
                    letterSpacing = 0.12f
                    setTextColor(0xFF8D80C0.toInt())
                })
                addView(TextView(activity).apply {
                    text = "Hey, ${state.displayName} 👋"
                    textSize = 28f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(0xFF4A3FA2.toInt())
                }, margins(LinearLayout.LayoutParams(-1, -2), 0, 8, 0, 0))
                addView(TextView(activity).apply {
                    text = "Your feelings are valid, always."
                    textSize = 16f
                    setTextColor(0xFF7D71B7.toInt())
                })
            }, FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM or Gravity.START))
        }

        override fun onDraw(canvas: Canvas) {
            val width = width
            val height = height
            paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(0xFFD6C2F3.toInt(), 0xFFBCEBE3.toInt()), null, Shader.TileMode.CLAMP)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            paint.shader = null
            paint.color = 0x25A5B2DA
            canvas.drawCircle(width * 0.90f, height * 0.55f, dp(62).toFloat(), paint)
        }
    }

    private fun sectionLabel(label: String): TextView = TextView(activity).apply {
        text = label
        textSize = 13f
        typeface = Typeface.DEFAULT_BOLD
        letterSpacing = 0.08f
        setTextColor(0xFFAAA0C1.toInt())
    }

    private fun cardBackground(radius: Int): GradientDrawable = solid(0xFFFFFFFF.toInt(), radius)

    private fun progressDrawableForBar(): GradientDrawable {
        return GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(0xFF7B58B7.toInt(), 0xFF55A3E0.toInt())).apply {
            cornerRadius = dp(8).toFloat()
        }
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

    data class State(
        val displayName: String,
        val streakDays: Int,
        val bestStreakDays: Int,
        val points: Int,
        val level: Int,
        val nextLevelAt: Int,
        val progressPercent: Int,
        val pointsToNextLevel: Int,
        val latestMoodIcon: String,
        val latestMoodTitle: String,
        val latestMoodNote: String,
        val latestMoodDate: String,
    )

    interface Actions {
        fun home()
        fun logMood()
        fun writeReflection()
        fun openMoodWall()
        fun openProfile()
    }
}
