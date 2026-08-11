package com.pulsefit.app.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pulsefit.app.MainActivity
import com.pulsefit.app.R
import java.util.concurrent.TimeUnit

/**
 * Home screen widget showing workout progress at a glance.
 *
 * Displays:
 *   - Weekly streak (consecutive days with a workout)
 *   - This week's workout count
 *   - Next workout hint
 *
 * Data is refreshed by WidgetUpdateWorker which has Hilt injection
 * for database access.
 */
class WorkoutWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_workout)

            // Set click action — open the app
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_workout_root, pendingIntent)

            // Initial placeholder text
            views.setTextViewText(R.id.widget_streak, "Loading...")
            views.setTextViewText(R.id.widget_weekly_summary, "")
            views.setTextViewText(R.id.widget_next_workout, "Open PulseFit to get started")

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        // Schedule a one-time worker to fetch real data
        val workRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
            .setInitialDelay(1, TimeUnit.SECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "widget_update",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    override fun onEnabled(context: Context) {
        // Schedule periodic updates when the first widget is added
        val periodicRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
            30, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "widget_periodic_update",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    override fun onDisabled(context: Context) {
        // Cancel all widget work when the last widget is removed
        WorkManager.getInstance(context).cancelUniqueWork("widget_periodic_update")
        WorkManager.getInstance(context).cancelUniqueWork("widget_update")
    }
}
