package com.pulsefit.app.ui.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pulsefit.app.R
import com.pulsefit.app.domain.repository.WorkoutRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.ZoneId

/**
 * Periodic worker that updates the home screen widget with fresh workout data.
 * Uses Hilt injection to access the WorkoutRepository.
 */
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workoutRepository: WorkoutRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
            val widgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(applicationContext, WorkoutWidgetProvider::class.java)
            )

            if (widgetIds.isEmpty()) return Result.success()

            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)

            // Get this week's workouts
            val weekStart = today.with(java.time.DayOfWeek.MONDAY)
            val weekEnd = today.plusDays(1)
            val startMillis = weekStart.atStartOfDay(zone).toInstant().toEpochMilli()
            val endMillis = weekEnd.atStartOfDay(zone).toInstant().toEpochMilli()

            val recentWorkouts = workoutRepository.getWorkoutsInDateRange(startMillis, endMillis)
            val workoutsThisWeek = recentWorkouts.size

            // Calculate streak
            val streakStart = today.minusDays(14)
            val streakStartMillis = streakStart.atStartOfDay(zone).toInstant().toEpochMilli()
            val streakWorkouts = workoutRepository.getWorkoutsInDateRange(streakStartMillis, endMillis)

            val workoutDates = streakWorkouts
                .map { it.startTime.atZone(zone).toLocalDate() }
                .toSet()

            var streak = 0
            var checkDate = today
            while (workoutDates.contains(checkDate)) {
                streak++
                checkDate = checkDate.minusDays(1)
            }

            for (widgetId in widgetIds) {
                val views = RemoteViews(applicationContext.packageName, R.layout.widget_workout)

                val streakText = if (streak > 0) "🔥 $streak day streak" else "Start your streak!"
                val weeklyText = when (workoutsThisWeek) {
                    0 -> "No workouts this week"
                    1 -> "1 workout this week"
                    else -> "$workoutsThisWeek workouts this week"
                }
                val nextText = if (workoutDates.contains(today)) {
                    "✅ Workout done today — great job!"
                } else {
                    "Tap to start your workout"
                }

                views.setTextViewText(R.id.widget_streak, streakText)
                views.setTextViewText(R.id.widget_weekly_summary, weeklyText)
                views.setTextViewText(R.id.widget_next_workout, nextText)

                appWidgetManager.updateAppWidget(widgetId, views)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
