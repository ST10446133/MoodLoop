package com.moodloop.app

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MoodWallView(
    private val activity: Activity,
    private val state: State,
    private val actions: Actions,
) : LinearLayout(activity) {
    private var currentFilter = state.filter
    private val filterChips = mutableMapOf<String, TextView>()
    private val helpedPosts = mutableSetOf<String>()
    private lateinit var postsContainer: LinearLayout

    init {
        orientation = VERTICAL
        setBackgroundColor(0xFFF8F1FF.toInt())

        val scroll = ScrollView(activity).apply {
            isFillViewport = false
            setBackgroundColor(0xFFF8F1FF.toInt())
        }
        addView(scroll, LayoutParams(-1, 0, 1f))

        val content = vertical().apply {
            setPadding(dp(10), dp(30), dp(10), dp(12))
        }
        scroll.addView(content, LayoutParams(-1, -2))

        content.addView(TextView(activity).apply {
            text = "Mood Wall"
            textSize = 30f
            typeface = Typeface.DEFAULT_BOLD
            includeFontPadding = false
            setTextColor(0xFF151238.toInt())
        })
        content.addView(TextView(activity).apply {
            text = "Anonymous reflections — no profiles, no public likes."
            textSize = 15f
            setTextColor(0xFF8D84B0.toInt())
        }, margins(LayoutParams(-1, -2), 0, 5, 0, 11))

        content.addView(filterRow())

        postsContainer = vertical()
        content.addView(postsContainer)
        renderPosts()

        addView(bottomNav(), LayoutParams(-1, dp(72)))
    }

    private fun filterRow(): HorizontalScrollView {
        val filters = listOf("All", "😊 Happy", "😌 Calm", "🤩 Excited", "😢 Sad", "😠 Angry", "😰 Anxious", "😤 Stressed", "😴 Tired", "😐 Neutral")
        return HorizontalScrollView(activity).apply wallFilters@ {
            isHorizontalScrollBarEnabled = false
            addView(LinearLayout(activity).apply {
                orientation = HORIZONTAL
                filters.forEach { label ->
                    val value = if (label == "All") "All" else label.substringAfter(" ")
                    addView(TextView(activity).apply {
                        text = label
                        textSize = 14f
                        typeface = Typeface.DEFAULT_BOLD
                        gravity = Gravity.CENTER
                        styleFilterChip(this, value == currentFilter)
                        setOnClickListener {
                            currentFilter = value
                            refreshFilterChips()
                            renderPosts()
                        }
                        filterChips[value] = this
                    }, margins(LayoutParams(-2, dp(34)), 0, 0, 8, 0).apply {
                        width = if (label == "All") dp(48) else dp(104)
                    })
                }
            })
        }
    }

    private fun refreshFilterChips() {
        filterChips.forEach { (value, chip) ->
            styleFilterChip(chip, value == currentFilter)
        }
    }

    private fun styleFilterChip(chip: TextView, selected: Boolean) {
        chip.setTextColor(if (selected) 0xFFFFFFFF.toInt() else 0xFF7A58B7.toInt())
        chip.background = if (selected) solid(0xFF7368F0.toInt(), 16) else solid(0xFFFFFFFF.toInt(), 16)
        chip.elevation = if (selected) dp(2).toFloat() else 0f
    }

    private fun renderPosts() {
        if (!::postsContainer.isInitialized) return
        postsContainer.removeAllViews()
        state.posts
            .filter { currentFilter == "All" || it.emotion == currentFilter }
            .forEach { post ->
                postsContainer.addView(postCard(post), margins(LayoutParams(-1, -2), 0, 12, 0, 0))
            }
    }

    private fun postCard(post: Post): LinearLayout {
        return vertical().apply {
            background = solid(0xFFFFFFFF.toInt(), 14)
            elevation = dp(2).toFloat()
            setPadding(dp(16), dp(14), dp(16), dp(14))

            addView(LinearLayout(activity).apply {
                orientation = HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                addView(TextView(activity).apply {
                    text = if (currentFilter == "All") {
                        "${moodIcon(post.emotion)} Anonymous · ${post.whenText}"
                    } else {
                        "Anonymous · ${post.whenText}"
                    }
                    textSize = 13f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(0xFF9C91B8.toInt())
                }, LayoutParams(0, -2, 1f))
                addView(TextView(activity).apply {
                    text = "🌐 shared"
                    textSize = 12f
                    typeface = Typeface.DEFAULT_BOLD
                    gravity = Gravity.CENTER
                    setTextColor(0xFF7D72F2.toInt())
                    background = solid(0xFFF0F2FF.toInt(), 12)
                }, LayoutParams(dp(74), dp(25)))
            })

            addView(TextView(activity).apply {
                text = post.text
                textSize = 17f
                setTextColor(0xFF4A4478.toInt())
            }, margins(LayoutParams(-1, -2), 0, 12, 0, 13))

            addView(helpedButton(post), LayoutParams(dp(138), dp(32)))
        }
    }

    private fun helpedButton(post: Post): TextView {
        return TextView(activity).apply {
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            styleHelpedButton(this, helpedPosts.contains(post.id))
            setOnClickListener {
                val nowHelped = if (helpedPosts.contains(post.id)) {
                    helpedPosts.remove(post.id)
                    false
                } else {
                    helpedPosts.add(post.id)
                    actions.helped(post.id)
                    true
                }
                styleHelpedButton(this, nowHelped)
            }
        }
    }

    private fun styleHelpedButton(button: TextView, helped: Boolean) {
        button.text = if (helped) "💜 Helped" else "💛 This helped me"
        button.setTextColor(if (helped) 0xFF7A58B7.toInt() else 0xFF8E84AF.toInt())
        button.background = solid(if (helped) 0xFFEDE7FF.toInt() else 0xFFF5F8FF.toInt(), 15)
        button.elevation = if (helped) dp(1).toFloat() else 0f
    }

    private fun bottomNav(): LinearLayout {
        val items = listOf(
            NavItem("home", "Home") { actions.home() },
            NavItem("log", "Log") { actions.log() },
            NavItem("journal", "Journal") { actions.journal() },
            NavItem("wall", "Wall") { },
            NavItem("me", "Me") { actions.me() },
        )
        return LinearLayout(activity).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(4), dp(3), dp(4), dp(12))
            setBackgroundColor(0xFFFFFFFF.toInt())
            items.forEach { item ->
                val active = item.label == "Wall"
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

    private fun vertical(): LinearLayout = LinearLayout(activity).apply { orientation = VERTICAL }

    private fun margins(params: LayoutParams, left: Int, top: Int, right: Int, bottom: Int): LayoutParams {
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom))
        return params
    }

    private fun solid(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
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

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    data class State(
        val filter: String,
        val posts: List<Post>,
    )

    data class Post(
        val id: String,
        val emotion: String,
        val text: String,
        val whenText: String,
    )

    interface Actions {
        fun helped(postId: String)
        fun home()
        fun log()
        fun journal()
        fun me()
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
