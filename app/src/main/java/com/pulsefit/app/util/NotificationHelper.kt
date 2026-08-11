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
    const val CHANNEL_QUICK_LAUNCH = "pulsefit_quick_launch"
    const val CHANNEL_MICRO_WORKOUT = "pulsefit_micro_workout"

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

        val quickLaunchChannel = NotificationChannel(
            CHANNEL_QUICK_LAUNCH, "Quick Launch", NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Pre-workout quick launch reminders" }

        val microWorkoutChannel = NotificationChannel(
            CHANNEL_MICRO_WORKOUT, "Micro Workouts", NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Micro workout movement nudges" }

        manager.createNotificationChannels(listOf(reminderChannel, streakChannel, weeklyChannel, accountabilityChannel, quickLaunchChannel, microWorkoutChannel))
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

    fun buildQuickLaunchNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_QUICK_LAUNCH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Workout starting soon")
            .setContentText("Your workout is in 5 minutes. Tap to jump right in.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
    }

    fun buildMicroWorkoutNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_MICRO_WORKOUT)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Movement snack time")
            .setContentText("2 minutes of movement. No changing clothes needed.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
