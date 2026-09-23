package com.moodloop.app

import android.app.Activity
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.util.Locale

class CreateAccountView(
    private val activity: Activity,
    private val actions: Actions,
) : ScrollView(activity) {
    private val name: EditText
    private val email: EditText
    private val password: EditText
    private val confirmPassword: EditText

    init {
        isFillViewport = true
        setBackgroundColor(0xFFF8F1FF.toInt())

        val screen = vertical().apply { setBackgroundColor(0xFFF8F1FF.toInt()) }
        addView(screen, LayoutParams(-1, -1))

        val header = vertical().apply {
            setPadding(dp(20), dp(38), dp(20), dp(18))
            gravity = Gravity.BOTTOM or Gravity.START
            background = gradient(0xFFB9F0D4.toInt(), 0xFFD4C4F4.toInt(), GradientDrawable.Orientation.TOP_BOTTOM, 0)
        }
        screen.addView(header, LinearLayout.LayoutParams(-1, dp(170)))

        header.addView(TextView(activity).apply {
            text = "🌱"
            textSize = 25f
            gravity = Gravity.START
        }, LinearLayout.LayoutParams(-1, dp(35)))

        header.addView(TextView(activity).apply {
            text = "Create account"
            setTextColor(0xFF4A3FA2.toInt())
            textSize = 32f
            typeface = Typeface.DEFAULT_BOLD
            includeFontPadding = false
        }, LinearLayout.LayoutParams(-1, -2))

        header.addView(TextView(activity).apply {
            text = "Start your private reflection journey today."
            setTextColor(0xFF8C82B6.toInt())
            textSize = 17f
            setPadding(0, dp(8), 0, 0)
        }, LinearLayout.LayoutParams(-1, -2))

        val form = vertical().apply { setPadding(dp(18), dp(19), dp(18), dp(18)) }
        screen.addView(form, LinearLayout.LayoutParams(-1, -2))

        name = field("Your name", false)
        email = field("Email address", false)
        password = field("Password", true)
        confirmPassword = field("Confirm password", true)

        form.addView(labeledField("Your name", name))
        form.addView(labeledField("Email address", email))
        form.addView(labeledField("Password", password))
        form.addView(labeledField("Confirm password", confirmPassword))

        form.addView(actionButton("Create account").apply {
            setOnClickListener {
                actions.createAccount(FormData(
                    name = name.text.toString(),
                    email = email.text.toString(),
                    password = password.text.toString(),
                    confirmPassword = confirmPassword.text.toString(),
                ))
            }
        }, margins(LinearLayout.LayoutParams(-1, dp(56)), 0, 10, 0, 0))

        form.addView(googleButton().apply {
            setOnClickListener { actions.googleSignIn() }
        }, margins(LinearLayout.LayoutParams(-1, dp(52)), 0, 12, 0, 0))

        val signInRow = LinearLayout(activity).apply {
            gravity = Gravity.CENTER
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dp(13), 0, 0)
        }
        signInRow.addView(TextView(activity).apply {
            text = "Already have an account? "
            setTextColor(0xFF8A82A7.toInt())
            textSize = 17f
        })
        signInRow.addView(TextView(activity).apply {
            text = "Sign in"
            setTextColor(0xFF4D38C7.toInt())
            typeface = Typeface.DEFAULT_BOLD
            textSize = 17f
            setOnClickListener { actions.signIn() }
        })
        form.addView(signInRow, LinearLayout.LayoutParams(-1, -2))

    }

    private fun labeledField(label: String, editText: EditText): LinearLayout {
        val box = vertical().apply { setPadding(0, 0, 0, dp(11)) }
        box.addView(TextView(activity).apply {
            text = label
            setTextColor(0xFF716887.toInt())
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
        }, LinearLayout.LayoutParams(-1, -2))

        val fieldView = if (label.lowercase(Locale.US).contains("password")) passwordBox(editText) else editText
        box.addView(fieldView, margins(LinearLayout.LayoutParams(-1, dp(52)), 0, 7, 0, 0))
        return box
    }

    private fun passwordBox(editText: EditText): FrameLayout {
        return FrameLayout(activity).apply {
            addView(editText, FrameLayout.LayoutParams(-1, -1))
            addView(TextView(activity).apply {
                text = "Show"
                setTextColor(0xFF9C95AA.toInt())
                textSize = 16f
                gravity = Gravity.CENTER
                isSingleLine = true
                setPadding(dp(8), 0, dp(10), 0)
                setOnClickListener { togglePassword(editText, this) }
            }, FrameLayout.LayoutParams(dp(78), -1, Gravity.END or Gravity.CENTER_VERTICAL))
        }
    }

    private fun field(hint: String, hidden: Boolean): EditText {
        return EditText(activity).apply {
            textSize = 19f
            isSingleLine = true
            setPadding(dp(13), 0, if (hidden) dp(78) else dp(13), 0)
            background = inputBackground()
            inputType = if (hidden) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or if (hint.lowercase(Locale.US).contains("email")) InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS else 0
            }
        }
    }

    private fun togglePassword(editText: EditText, show: TextView) {
        val hidden = show.text == "Show"
        editText.inputType = InputType.TYPE_CLASS_TEXT or if (hidden) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD
        editText.setSelection(editText.text.length)
        show.text = if (hidden) "Hide" else "Show"
    }

    private fun actionButton(textValue: String): TextView {
        return TextView(activity).apply {
            text = textValue
            gravity = Gravity.CENTER
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(0xFFFFFFFF.toInt())
            background = gradient(0xFF5AB998.toInt(), 0xFF55A3E0.toInt(), GradientDrawable.Orientation.LEFT_RIGHT, 13)
            elevation = dp(2).toFloat()
        }
    }

    private fun googleButton(): TextView = TextView(activity).apply {
        text = "G  Continue with Google"
        gravity = Gravity.CENTER
        textSize = 17f
        typeface = Typeface.DEFAULT_BOLD
        setTextColor(0xFF4A4478.toInt())
        background = solid(0xFFFFFFFF.toInt(), 13).apply {
            setStroke(dp(1), 0xFFE1DAEA.toInt())
        }
        elevation = dp(1).toFloat()
    }

    private fun inputBackground(): GradientDrawable = solid(0xFFFFFFFF.toInt(), 13).apply {
        setStroke(dp(1), 0xFFF0EAF8.toInt())
    }

    private fun vertical(): LinearLayout = LinearLayout(activity).apply { orientation = LinearLayout.VERTICAL }

    private fun margins(params: LinearLayout.LayoutParams, left: Int, top: Int, right: Int, bottom: Int): LinearLayout.LayoutParams {
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom))
        return params
    }

    private fun solid(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun gradient(start: Int, end: Int, orientation: GradientDrawable.Orientation, radius: Int): GradientDrawable =
        GradientDrawable(orientation, intArrayOf(start, end)).apply { cornerRadius = dp(radius).toFloat() }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    interface Actions {
        fun createAccount(form: FormData)
        fun googleSignIn()
        fun signIn()
    }

    data class FormData(
        val name: String,
        val email: String,
        val password: String,
        val confirmPassword: String,
    )
}
