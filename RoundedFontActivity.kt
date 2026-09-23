package com.moodloop.app

import android.app.Activity
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

open class RoundedFontActivity : Activity() {
    private val moodloopTypeface: Typeface by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resources.getFont(R.font.ubuntu_regular)
        } else {
            Typeface.create("sans-serif-rounded", Typeface.NORMAL)
        }
    }

    private val moodloopBoldTypeface: Typeface by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resources.getFont(R.font.ubuntu_bold)
        } else {
            Typeface.create("sans-serif-rounded", Typeface.BOLD)
        }
    }

    override fun setContentView(view: View?) {
        view?.applyRoundedFont()
        super.setContentView(view)
    }

    private fun View.applyRoundedFont() {
        if (this is TextView) {
            typeface = if (typeface?.isBold == true) moodloopBoldTypeface else moodloopTypeface
            val sizeSp = textSize / resources.displayMetrics.scaledDensity
            val adjustedSize = when {
                sizeSp <= 16f -> sizeSp + 2f
                sizeSp <= 20f -> sizeSp + 1.5f
                sizeSp < 24f -> sizeSp + 1f
                else -> sizeSp
            }
            if (adjustedSize != sizeSp) {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, adjustedSize)
            }
        }
        if (this is ViewGroup) {
            for (index in 0 until childCount) {
                getChildAt(index).applyRoundedFont()
            }
        }
    }
}
