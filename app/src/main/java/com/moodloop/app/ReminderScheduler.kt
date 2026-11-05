package com.moodloop.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar
import java.util.Locale

object ReminderScheduler {
    private const val requestCode = 4218

    fun applyReminder(context: Context, enabled: Boolean, time: String): Boolean {
        if (!enabled) {
            cancel(context)
            return true
        }

        val parts = time.trim().split(":")
        if (parts.size != 2) return false
        val hour = parts[0].toIntOrNull() ?: return false
        val minute = parts[1].toIntOrNull() ?: return false
        if (hour !in 0..23 || minute !in 0..59) return false

        schedule(context, hour, minute)
        return true
    }

    fun scheduleNext(context: Context, hour: Int, minute: Int) {
        if (hour in 0..23 && minute in 0..59) schedule(context, hour, minute)
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent(context))
    }

    private fun nextTriggerAt(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance(Locale.getDefault()).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }

    private fun schedule(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = pendingIntent(context, hour, minute)
        val triggerAt = nextTriggerAt(hour, minute)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, intent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, intent)
            }
        } catch (error: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, intent)
        }
    }

    private fun pendingIntent(context: Context, hour: Int = -1, minute: Int = -1): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java)
            .putExtra("hour", hour)
            .putExtra("minute", minute)
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
