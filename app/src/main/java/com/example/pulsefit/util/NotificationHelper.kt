package com.example.pulsefit.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.pulsefit.R

object NotificationHelper {

    const val CHANNEL_REMINDERS = "pulsefit_reminders"
    const val CHANNEL_STREAK = "pulsefit_streak"
    const val CHANNEL_WEEKLY = "pulsefit_weekly"

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

        manager.createNotificationChannels(listOf(reminderChannel, streakChannel, weeklyChannel))
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
