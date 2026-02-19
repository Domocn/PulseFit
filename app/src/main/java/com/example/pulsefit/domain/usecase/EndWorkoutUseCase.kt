package com.example.pulsefit.domain.usecase

import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.domain.repository.UserRepository
import com.example.pulsefit.domain.repository.WorkoutRepository
import java.time.Instant
import javax.inject.Inject

class EndWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        workoutId: Long,
        burnPoints: Int,
        zoneTime: Map<HeartRateZone, Long>
    ) {
        val workout = workoutRepository.getWorkoutById(workoutId) ?: return
        val now = Instant.now()
        val durationSeconds = (now.toEpochMilli() - workout.startTime.toEpochMilli()) / 1000
        val readings = workoutRepository.getReadingsForWorkoutOnce(workoutId)
        val avgHr = if (readings.isNotEmpty()) readings.map { it.heartRate }.average().toInt() else 0
        val maxHr = readings.maxOfOrNull { it.heartRate } ?: 0

        workoutRepository.updateWorkout(
            workout.copy(
                endTime = now,
                durationSeconds = durationSeconds.toInt(),
                burnPoints = burnPoints,
                averageHeartRate = avgHr,
                maxHeartRate = maxHr,
                zoneTime = zoneTime
            )
        )

        // Update user stats
        userRepository.incrementWorkoutCount(burnPoints, now.toEpochMilli())
    }
}
