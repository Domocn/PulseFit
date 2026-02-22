package com.pulsefit.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.pulsefit.app.R

object NotificationHelper {

    const val CHANNEL_REMINDERS = "pulsefit_reminders"
    const val CHANNEL_STREAK = "pulsefit_streak"
    const val CHANNEL_WEEKLY = "pulsefit_weekly"
    const val CHANNEL_ACCOUNTABILITY = "pulsefit_accountability"

    fun createChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val reminderChannel = NotificationChannel(
            CHANNEL_REMINDERS, "Workout Reminders", NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Daily workout reminders" }

        val streakChannel = NotificationChannel(
            CHANNEL_STREAK, "Streak Alerts", NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Streak at risk notifications" }

        val weeklyChannel = NotificationChannel(
            CHANNEL_WEEKLY, "Weekly Summary", NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Weekly workout summary" }

        val accountabilityChannel = NotificationChannel(
            CHANNEL_ACCOUNTABILITY, "Accountability Alarm", NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Escalating workout reminders" }

        manager.createNotificationChannels(listOf(reminderChannel, streakChannel, weeklyChannel, accountabilityChannel))
    }

    fun buildReminderNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Time to move!")
            .setContentText("Your daily workout is waiting. Even 5 minutes counts.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
    }

    fun buildStreakNotification(context: Context, streak: Int): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_STREAK)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Streak at risk!")
            .setContentText("Your $streak-day streak will end tonight. Just 5 minutes?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
    }

    fun buildAccountabilityNotification(context: Context, level: Int): NotificationCompat.Builder {
        val (title, message, priority) = when (level) {
            1 -> Triple("Workout Reminder", "Your workout is waiting for you", NotificationCompat.PRIORITY_DEFAULT)
            2 -> Triple("Quick Check-in", "Ready to move? Even a short session counts.", NotificationCompat.PRIORITY_DEFAULT)
            3 -> Triple("Hey!", "Just 5 minutes. That's all it takes.", NotificationCompat.PRIORITY_HIGH)
            else -> Triple("Last Reminder", "No pressure. Tomorrow works too.", NotificationCompat.PRIORITY_HIGH)
        }
        return NotificationCompat.Builder(context, CHANNEL_ACCOUNTABILITY)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(priority)
            .setAutoCancel(true)
    }

    fun buildWeeklySummaryNotification(
        context: Context,
        workouts: Int,
        points: Int
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_WEEKLY)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Weekly Summary")
            .setContentText("$workouts workouts, $points burn points this week!")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
    }
}
