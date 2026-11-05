package com.moodloop.app

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import android.widget.TextView

class SettingsView(private val activity: Activity, private val actions: Actions, private val language: String) : LinearLayout(activity) {
    private val copy = when (language) {
        "zu" -> mapOf(
            "Settings" to "Izilungiselelo", "ACCOUNT" to "I-AKHAWUNTI", "PREFERENCES" to "OKUNCANYELWAYO",
            "Manage your MoodLoop" to "Phatha i-MoodLoop yakho", "Account, password, and reminder controls live here." to "Izilawuli ze-akhawunti, iphasiwedi nezikhumbuzi zilapha.",
            "A calm place to manage your account." to "Indawo ezolile yokuphatha i-akhawunti yakho.",
            "Edit display name" to "Hlela igama eliboniswayo", "Change how your name appears in the app." to "Shintsha indlela igama lakho elivela ngayo kuhlelo lokusebenza.",
            "Change password" to "Shintsha iphasiwedi", "Update your Firebase sign-in password." to "Buyekeza iphasiwedi yakho yokungena ye-Firebase.",
            "Reminder settings" to "Izilungiselelo zezikhumbuzi", "Choose when MoodLoop should remind you." to "Khetha ukuthi i-MoodLoop ikukhumbuze nini.",
            "Language" to "Ulimi", "Choose the language used throughout MoodLoop." to "Khetha ulimi olusetshenziswa kuyo yonke i-MoodLoop.",
            "Log out" to "Phuma", "Leave this account on this device." to "Susa le akhawunti kule divayisi.",
            "Delete account" to "Susa i-akhawunti", "Permanently remove this account from MoodLoop." to "Susa le akhawunti ku-MoodLoop unomphela.",
            "Overview" to "Uhlolojikelele", "Tracker" to "Umlandeleli", "Badges" to "Amabheji", "Settings" to "Izilungiselelo",
            "Choose language" to "Khetha ulimi", "Cancel" to "Khansela", "Home" to "Ikhaya", "Log" to "Bhala", "Journal" to "Ijenali", "Wall" to "Udonga", "Me" to "Mina"
        )
        "af" -> mapOf(
            "Settings" to "Instellings", "ACCOUNT" to "REKENING", "PREFERENCES" to "VOORKEURE",
            "Manage your MoodLoop" to "Bestuur jou MoodLoop", "Account, password, and reminder controls live here." to "Rekening-, wagwoord- en herinneringbeheer is hier.",
            "A calm place to manage your account." to "’n Rustige plek om jou rekening te bestuur.",
            "Edit display name" to "Wysig vertoonnaam", "Change how your name appears in the app." to "Verander hoe jou naam in die toepassing verskyn.",
            "Change password" to "Verander wagwoord", "Update your Firebase sign-in password." to "Dateer jou Firebase-aanmeldwagwoord op.",
            "Reminder settings" to "Herinneringinstellings", "Choose when MoodLoop should remind you." to "Kies wanneer MoodLoop jou moet herinner.",
            "Language" to "Taal", "Choose the language used throughout MoodLoop." to "Kies die taal wat oral in MoodLoop gebruik word.",
            "Log out" to "Teken af", "Leave this account on this device." to "Verwyder hierdie rekening van hierdie toestel.",
            "Delete account" to "Verwyder rekening", "Permanently remove this account from MoodLoop." to "Verwyder hierdie rekening permanent uit MoodLoop.",
            "Overview" to "Oorsig", "Tracker" to "Nasporing", "Badges" to "Kentekens", "Settings" to "Instellings",
            "Choose language" to "Kies taal", "Cancel" to "Kanselleer", "Home" to "Tuis", "Log" to "Aanteken", "Journal" to "Joernaal", "Wall" to "Muur", "Me" to "Ek"
        )
        else -> emptyMap()
    }
    private fun tr(value: String) = copy[value] ?: value
    init {
        orientation = VERTICAL
        setBackgroundColor(0xFFF8F1FF.toInt())
        val scroll = ScrollView(activity)
        addView(scroll, LayoutParams(-1, 0, 1f))
        val content = vertical().apply { setPadding(dp(18), dp(34), dp(18), dp(24)) }
        scroll.addView(content)
        content.addView(title(tr("Settings")))
        content.addView(TextView(activity).apply {
            text = tr("A calm place to manage your account.")
            textSize = 14f
            setTextColor(0xFF8E84AF.toInt())
        }, margins(LayoutParams(-1, -2), 0, 3, 0, 0))
        content.addView(tabs(), margins(LayoutParams(-1, dp(48)), 0, 13, 0, 0))
        content.addView(hero(), margins(LayoutParams(-1, -2), 0, 6, 0, 0))
        content.addView(sectionLabel(tr("ACCOUNT")), margins(LayoutParams(-1, -2), 0, 22, 0, 0))
        content.addView(setting("👤", tr("Edit display name"), tr("Change how your name appears in the app.")) { actions.editDisplayName() }, margins(LayoutParams(-1, -2), 0, 9, 0, 0))
        content.addView(setting("🔐", tr("Change password"), tr("Update your Firebase sign-in password.")) { actions.changePassword() }, margins(LayoutParams(-1, -2), 0, 9, 0, 0))
        content.addView(sectionLabel(tr("PREFERENCES")), margins(LayoutParams(-1, -2), 0, 20, 0, 0))
        content.addView(setting("🔔", tr("Reminder settings"), tr("Choose when MoodLoop should remind you.")) { actions.reminders() }, margins(LayoutParams(-1, -2), 0, 9, 0, 0))
        content.addView(setting("🌐", tr("Language"), tr("Choose the language used throughout MoodLoop.")) { chooseLanguage() }, margins(LayoutParams(-1, -2), 0, 9, 0, 0))
        content.addView(setting("🚪", tr("Log out"), tr("Leave this account on this device.")) { actions.logout() }, margins(LayoutParams(-1, -2), 0, 9, 0, 0))
        content.addView(setting("🗑", tr("Delete account"), tr("Permanently remove this account from MoodLoop.")) { actions.deleteAccount() }, margins(LayoutParams(-1, -2), 0, 9, 0, 0))
        addView(ProfileMiniNav(activity, "Me", object : ProfileMiniNav.Actions {
            override fun home() = actions.home(); override fun log() = actions.log(); override fun journal() = actions.journal(); override fun wall() = actions.wall()
        }, ::dp, ::tr), LayoutParams(-1, dp(72)))
    }

    private fun chooseLanguage() {
        val codes = listOf("en", "zu", "af")
        val names = listOf("English", "isiZulu", "Afrikaans")
        val selected = codes.indexOf(language).coerceAtLeast(0)
        val dialog = AlertDialog.Builder(activity).create()
        dialog.setView(languageDialogContent(names, codes, selected, onSelect = { code ->
            dialog.dismiss()
            if (code != language) actions.changeLanguage(code)
        }, onCancel = { dialog.dismiss() }))
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout((resources.displayMetrics.widthPixels - dp(40)).coerceAtMost(dp(380)), -2)
    }

    private fun languageDialogContent(
        names: List<String>,
        codes: List<String>,
        selected: Int,
        onSelect: (String) -> Unit,
        onCancel: () -> Unit,
    ): LinearLayout = vertical().apply {
        background = solid(0xFFF8F1FF.toInt(), 22)
        elevation = dp(8).toFloat()
        setPadding(dp(20), dp(20), dp(20), dp(16))
        addView(TextView(activity).apply {
            text = "🌐"
            textSize = 27f
            gravity = Gravity.CENTER
            setTextColor(0xFF6F61F2.toInt())
        }, LayoutParams(-1, dp(42)))
        addView(TextView(activity).apply {
            text = tr("Choose language")
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(0xFF151238.toInt())
        }, margins(LayoutParams(-1, -2), 0, 7, 0, 0))
        addView(TextView(activity).apply {
            text = tr("Choose the language used throughout MoodLoop.")
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(0xFF746A98.toInt())
        }, margins(LayoutParams(-1, -2), 0, 4, 0, 14))
        names.forEachIndexed { index, name ->
            val active = index == selected
            addView(LinearLayout(activity).apply {
                orientation = HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(14), 0, dp(14), 0)
                background = GradientDrawable().apply {
                    setColor(if (active) 0xFFEDE9FF.toInt() else Color.WHITE)
                    cornerRadius = dp(14).toFloat()
                    setStroke(dp(if (active) 2 else 1), if (active) 0xFF7B6CF2.toInt() else 0xFFE8E1F2.toInt())
                }
                setOnClickListener { onSelect(codes[index]) }
                addView(TextView(activity).apply {
                    text = name
                    textSize = 17f
                    typeface = if (active) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                    setTextColor(if (active) 0xFF4A3FA2.toInt() else 0xFF322C5E.toInt())
                }, LayoutParams(0, -2, 1f))
                addView(TextView(activity).apply {
                    text = if (active) "✓" else "○"
                    textSize = 19f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(if (active) 0xFF6F61F2.toInt() else 0xFFC2B9D5.toInt())
                })
            }, margins(LayoutParams(-1, dp(52)), 0, if (index == 0) 0 else 8, 0, 0))
        }
        addView(TextView(activity).apply {
            text = tr("Cancel")
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(0xFF6F61F2.toInt())
            setPadding(0, dp(8), 0, 0)
            setOnClickListener { onCancel() }
        }, margins(LayoutParams(-1, dp(42)), 0, 8, 0, 0))
    }

    private fun title(value: String): TextView = TextView(activity).apply { text = value; textSize = 30f; typeface = Typeface.DEFAULT_BOLD; setTextColor(0xFF151238.toInt()) }
    private fun hero(): LinearLayout = LinearLayout(activity).apply {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(0xFFFFE9DF.toInt(), 0xFFE7DDFB.toInt())).apply { cornerRadius = dp(22).toFloat() }
        elevation = dp(3).toFloat()
        setPadding(dp(18), dp(18), dp(18), dp(18))
        addView(TextView(activity).apply {
            text = "⚙️"
            textSize = 25f
            gravity = Gravity.CENTER
            background = solid(0xBFFFFFFF.toInt(), 18)
        }, LayoutParams(dp(54), dp(54)))
        addView(vertical().apply {
            addView(TextView(activity).apply { text = tr("Manage your MoodLoop"); textSize = 16f; typeface = Typeface.DEFAULT_BOLD; setTextColor(0xFF4A3FA2.toInt()) })
            addView(TextView(activity).apply { text = tr("Account, password, and reminder controls live here."); textSize = 12f; setTextColor(0xFF746A98.toInt()) }, margins(LayoutParams(-1, -2), 0, 3, 0, 0))
        }, LayoutParams(0, -2, 1f).apply { marginStart = dp(14) })
    }
    private fun setting(icon: String, label: String, detail: String, action: () -> Unit): LinearLayout = LinearLayout(activity).apply {
        orientation = HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(dp(14), dp(12), dp(14), dp(12))
        minimumHeight = dp(76)
        val destructive = label == tr("Delete account")
        background = solid(if (destructive) 0xFFFFF7F6.toInt() else 0xFFFFFFFF.toInt(), 18)
        elevation = dp(2).toFloat(); setOnClickListener { action() }
        addView(TextView(activity).apply {
            text = icon; textSize = 21f; gravity = Gravity.CENTER
            background = solid(if (destructive) 0xFFFFE8E5.toInt() else 0xFFF1EDFF.toInt(), 15)
        }, LayoutParams(dp(46), dp(46)))
        addView(vertical().apply {
            addView(TextView(activity).apply { text = label; textSize = 16f; typeface = Typeface.DEFAULT_BOLD; setTextColor(if (destructive) 0xFFB94050.toInt() else 0xFF322C5E.toInt()) })
            addView(TextView(activity).apply { text = detail; textSize = 12f; setTextColor(0xFF8E84AF.toInt()) }, margins(LayoutParams(-1, -2), 0, 3, 0, 0))
        }, LayoutParams(0, -2, 1f).apply { marginStart = dp(13) })
    }
    private fun sectionLabel(value: String): TextView = TextView(activity).apply { text = value; textSize = 14f; typeface = Typeface.DEFAULT_BOLD; letterSpacing = 0.08f; setTextColor(0xFFA69DBD.toInt()) }
    private fun tabs(): HorizontalScrollView {
        val labels = listOf("Overview", "Tracker", "Badges", "Settings")
        return HorizontalScrollView(activity).apply {
            isHorizontalScrollBarEnabled = false
            clipToPadding = false
            addView(LinearLayout(activity).apply {
            orientation = HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            labels.forEach { label ->
                val active = label == "Settings"
                addView(TextView(activity).apply {
                    text = tr(label); textSize = 13f; typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER
                    setPadding(dp(16), 0, dp(16), 0)
                    setTextColor(if (active) 0xFFFFFFFF.toInt() else 0xFF756B94.toInt())
                    background = solid(if (active) 0xFF7368F0.toInt() else 0xFFFFFFFF.toInt(), 18)
                    elevation = if (active) dp(2).toFloat() else 0f
                    setOnClickListener { when (label) { "Overview" -> actions.overview(); "Tracker" -> actions.tracker(); "Badges" -> actions.badges() } }
                }, margins(LayoutParams(-2, dp(42)), 0, 0, 8, 0))
            }
            })
        }
    }
    private fun vertical(): LinearLayout = LinearLayout(activity).apply { orientation = VERTICAL }
    private fun margins(params: LayoutParams, left: Int, top: Int, right: Int, bottom: Int): LayoutParams { params.setMargins(dp(left), dp(top), dp(right), dp(bottom)); return params }
    private fun solid(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat() }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    interface Actions : ProfileMiniNav.Actions {
        fun overview(); fun tracker(); fun badges(); fun editDisplayName(); fun changePassword(); fun reminders(); fun logout(); fun deleteAccount(); fun changeLanguage(code: String)
    }
}
