package com.example.pulsefit.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pulsefit.domain.repository.UserRepository
import com.example.pulsefit.domain.repository.WorkoutRepository
import com.example.pulsefit.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.ZoneId

@HiltWorker
class StreakAtRiskWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        if (ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val profile = userRepository.getUserProfileOnce() ?: return Result.success()
        if (profile.currentStreak <= 0) return Result.success()

        // Check if user has worked out today
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val startMillis = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val todayWorkouts = workoutRepository.getWorkoutsInDateRange(startMillis, endMillis)

        if (todayWorkouts.isEmpty()) {
            val notification = NotificationHelper.buildStreakNotification(
                applicationContext, profile.currentStreak
            ).build()
            NotificationManagerCompat.from(applicationContext).notify(1002, notification)
        }

        return Result.success()
    }
}
