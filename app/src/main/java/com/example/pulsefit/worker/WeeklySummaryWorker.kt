package com.example.pulsefit.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pulsefit.domain.usecase.GetWorkoutStatsUseCase
import com.example.pulsefit.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeeklySummaryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getWorkoutStats: GetWorkoutStatsUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        if (ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val stats = getWorkoutStats.getWeeklyStats()
        if (stats.totalWorkouts > 0) {
            val notification = NotificationHelper.buildWeeklySummaryNotification(
                applicationContext, stats.totalWorkouts, stats.totalBurnPoints
            ).build()
            NotificationManagerCompat.from(applicationContext).notify(1003, notification)
        }

        return Result.success()
    }
}
