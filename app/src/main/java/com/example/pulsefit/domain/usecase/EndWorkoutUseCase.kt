package com.example.pulsefit.domain.usecase

import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.domain.repository.UserRepository
import com.example.pulsefit.domain.repository.WorkoutRepository
import com.example.pulsefit.health.HealthConnectRepository
import com.example.pulsefit.util.CalorieCalculator
import java.time.Instant
import javax.inject.Inject

class EndWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository,
    private val healthConnectRepository: HealthConnectRepository
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

        // Calculate estimated calories from user profile
        val profile = userRepository.getUserProfileOnce()
        val estimatedCalories = if (profile != null) {
            CalorieCalculator.estimate(
                avgHeartRate = avgHr,
                durationMinutes = (durationSeconds / 60).toInt(),
                age = profile.age,
                weightKg = profile.weight,
                isMale = profile.biologicalSex == "male"
            )
        } else null

        val updatedWorkout = workout.copy(
            endTime = now,
            durationSeconds = durationSeconds.toInt(),
            burnPoints = burnPoints,
            averageHeartRate = avgHr,
            maxHeartRate = maxHr,
            zoneTime = zoneTime,
            estimatedCalories = estimatedCalories
        )
        workoutRepository.updateWorkout(updatedWorkout)

        // Update user stats
        userRepository.incrementWorkoutCount(burnPoints, now.toEpochMilli())

        // Sync to Health Connect
        healthConnectRepository.writeWorkout(updatedWorkout, readings)
    }
}
