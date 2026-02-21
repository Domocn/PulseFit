package com.pulsefit.app.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.pulsefit.app.domain.repository.WorkoutRepository
import com.pulsefit.app.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

@HiltWorker
class AccountabilityAlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val workoutRepository: WorkoutRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        if (ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        // Check if user already worked out today
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val startMillis = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val todayWorkouts = workoutRepository.getWorkoutsInDateRange(startMillis, endMillis)

        if (todayWorkouts.isNotEmpty()) return Result.success()

        val level = inputData.getInt("level", 1)

        val notification = NotificationHelper.buildAccountabilityNotification(
            applicationContext, level
        ).build()
        NotificationManagerCompat.from(applicationContext).notify(2000 + level, notification)

        // Schedule next level if not at max
        if (level < 4) {
            val nextRequest = OneTimeWorkRequestBuilder<AccountabilityAlarmWorker>()
                .setInitialDelay(15, TimeUnit.MINUTES)
                .setInputData(Data.Builder().putInt("level", level + 1).build())
                .addTag("accountability_escalation")
                .build()
            WorkManager.getInstance(applicationContext).enqueue(nextRequest)
        }

        return Result.success()
    }
}
