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

class ProfileView(
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
        addView(scroll, LayoutParams(-1, 0, 1f))

        val content = vertical()
        scroll.addView(content, LayoutParams(-1, -2))
        content.addView(header(), LayoutParams(-1, dp(104)))
        content.addView(tabs(), LayoutParams(-1, dp(48)))

        val body = vertical().apply {
            setPadding(dp(14), dp(16), dp(14), dp(18))
        }
        content.addView(body, LayoutParams(-1, -2))
        body.addView(statsGrid())
        body.addView(progressCard(), margins(LayoutParams(-1, -2), 0, 14, 0, 0))
        body.addView(mostLoggedCard(), margins(LayoutParams(-1, -2), 0, 14, 0, 0))

        addView(bottomNav(), LayoutParams(-1, dp(72)))
    }

    private fun header(): FrameLayout {
        return ProfileHeader(activity).apply {
            setPadding(dp(16), dp(24), dp(16), dp(10))
            addView(TextView(activity).apply {
                text = state.initial
                textSize = 22f
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                setTextColor(0xFF322C5E.toInt())
                background = solid(0xFFFFFFFF.toInt(), 14)
                elevation = dp(3).toFloat()
            }, FrameLayout.LayoutParams(dp(54), dp(54), Gravity.START or Gravity.CENTER_VERTICAL))
            addView(vertical().apply {
                addView(TextView(activity).apply {
                    text = state.displayName
                    textSize = 22f
                    typeface = Typeface.DEFAULT_BOLD
                    includeFontPadding = false
                    setTextColor(0xFF151238.toInt())
                })
                addView(TextView(activity).apply {
                    text = state.email
                    textSize = 14f
                    setTextColor(0xFF7E73A0.toInt())
                }, margins(LayoutParams(-1, -2), 0, 4, 0, 0))
                addView(TextView(activity).apply {
                    text = "Level ${state.level} · ${state.points} pts · 🔥 ${state.currentStreak}"
                    textSize = 14f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(0xFF7A58B7.toInt())
                }, margins(LayoutParams(-1, -2), 0, 3, 0, 0))
            }, FrameLayout.LayoutParams(-1, -2, Gravity.CENTER_VERTICAL).apply {
                leftMargin = dp(70)
            })
        }
    }

    private fun tabs(): LinearLayout {
        val labels = listOf("Overview", "Tracker", "Badges", "Settings")
        return LinearLayout(activity).apply {
            orientation = HORIZONTAL
            setBackgroundColor(0xFFFFFFFF.toInt())
            labels.forEach { label ->
                val active = label == "Overview"
                addView(TextView(activity).apply {
                    text = label
                    textSize = 13f
                    typeface = Typeface.DEFAULT_BOLD
                    gravity = Gravity.CENTER
                    setTextColor(if (active) 0xFF6F61F2.toInt() else 0xFF9D94B8.toInt())
                    background = if (active) activeTabBackground() else null
                    setOnClickListener {
                        when (label) {
                            "Tracker" -> actions.tracker()
                            "Badges" -> actions.badges()
                            "Settings" -> actions.settings()
                        }
                    }
                }, LayoutParams(0, -1, 1f))
            }
        }
    }

    private fun statsGrid(): GridLayout {
        return GridLayout(activity).apply {
            columnCount = 2
            addView(statCard("📊", state.moodCount.toString(), "Mood entries"), cellParams(0))
            addView(statCard("🏅", state.achievementCount.toString(), "Achievements"), cellParams(1))
            addView(statCard("🔥", "${state.currentStreak}d", "Current streak"), cellParams(2))
            addView(statCard("⭐", "${state.bestStreak}d", "Best streak"), cellParams(3))
        }
    }

    private fun statCard(icon: String, value: String, label: String): LinearLayout {
        return vertical().apply {
            background = solid(0xFFFFFFFF.toInt(), 14)
            elevation = dp(2).toFloat()
            setPadding(dp(14), dp(11), dp(14), dp(10))
            addView(TextView(activity).apply {
                text = icon
                textSize = 24f
            })
            addView(TextView(activity).apply {
                text = value
                textSize = 24f
                typeface = Typeface.DEFAULT_BOLD
                includeFontPadding = false
                setTextColor(0xFF151238.toInt())
            }, margins(LayoutParams(-1, -2), 0, 5, 0, 0))
            addView(TextView(activity).apply {
                text = label
                textSize = 14f
                setTextColor(0xFF9D94B8.toInt())
            }, margins(LayoutParams(-1, -2), 0, 3, 0, 0))
        }
    }

    private fun progressCard(): LinearLayout {
        return vertical().apply {
            background = solid(0xFFFFFFFF.toInt(), 14)
            elevation = dp(2).toFloat()
            setPadding(dp(16), dp(13), dp(16), dp(13))
            addView(LinearLayout(activity).apply {
                orientation = HORIZONTAL
                addView(TextView(activity).apply {
                    text = "Level ${state.level}"
                    textSize = 15f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(0xFF5E548B.toInt())
                }, LayoutParams(0, -2, 1f))
                addView(TextView(activity).apply {
                    text = "${state.progressPercent}% to Level ${state.level + 1}"
                    textSize = 15f
                    typeface = Typeface.DEFAULT_BOLD
                    gravity = Gravity.END
                    setTextColor(0xFF8A80B2.toInt())
                })
            })
            addView(ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal).apply {
                max = 100
                progress = state.progressPercent
                progressDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(0xFF7B58B7.toInt(), 0xFF55A3E0.toInt())).apply {
                    cornerRadius = dp(8).toFloat()
                }
            }, margins(LayoutParams(-1, dp(9)), 0, 12, 0, 0))
            addView(TextView(activity).apply {
                text = "${state.pointsToNextLevel} pts to next level"
                textSize = 13f
                setTextColor(0xFF9D94B8.toInt())
            }, margins(LayoutParams(-1, -2), 0, 7, 0, 0))
        }
    }

    private fun mostLoggedCard(): LinearLayout {
        return LinearLayout(activity).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = solid(0xFFFFFFFF.toInt(), 14)
            elevation = dp(2).toFloat()
            setPadding(dp(16), dp(14), dp(16), dp(14))
            addView(TextView(activity).apply {
                text = moodIcon(state.topEmotion)
                textSize = 32f
                gravity = Gravity.CENTER
            }, LayoutParams(dp(48), dp(56)))
            addView(vertical().apply {
                addView(TextView(activity).apply {
                    text = "Most logged emotion"
                    textSize = 13f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(0xFFB0A8C8.toInt())
                })
                addView(TextView(activity).apply {
                    text = state.topEmotion
                    textSize = 20f
                    typeface = Typeface.DEFAULT_BOLD
                    includeFontPadding = false
                    setTextColor(0xFF322C5E.toInt())
                }, margins(LayoutParams(-1, -2), 0, 2, 0, 0))
                addView(TextView(activity).apply {
                    text = "${state.topEmotionCount} times"
                    textSize = 13f
                    setTextColor(0xFF8E84AF.toInt())
                }, margins(LayoutParams(-1, -2), 0, 4, 0, 0))
            }, LayoutParams(0, -2, 1f))
        }
    }

    private fun bottomNav(): LinearLayout {
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
                val active = item.label == "Me"
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

    private inner class ProfileHeader(activity: Activity) : FrameLayout(activity) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        init { setWillNotDraw(false) }

        override fun onDraw(canvas: Canvas) {
            paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(0xFFF9E5D9.toInt(), 0xFFD6C2F3.toInt()), null, Shader.TileMode.CLAMP)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            paint.shader = null
        }
    }

    private fun cellParams(index: Int): GridLayout.LayoutParams {
        val col = index % 2
        return GridLayout.LayoutParams().apply {
            width = 0
            height = dp(118)
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(
                if (col == 0) 0 else dp(6),
                if (index < 2) 0 else dp(10),
                if (col == 1) 0 else dp(6),
                0,
            )
        }
    }

    private fun activeTabBackground(): GradientDrawable = GradientDrawable().apply {
        setColor(0x00FFFFFF)
        setStroke(dp(2), 0xFF6F61F2.toInt())
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
        val username: String,
        val email: String,
        val initial: String,
        val level: Int,
        val points: Int,
        val currentStreak: Int,
        val bestStreak: Int,
        val moodCount: Int,
        val achievementCount: Int,
        val progressPercent: Int,
        val pointsToNextLevel: Int,
        val topEmotion: String,
        val topEmotionCount: Int,
    )

    interface Actions {
        fun home()
        fun log()
        fun journal()
        fun wall()
        fun tracker()
        fun badges()
        fun settings()
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
}
