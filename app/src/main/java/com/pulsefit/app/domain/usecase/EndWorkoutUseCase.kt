package com.pulsefit.app.domain.usecase

import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.domain.repository.UserRepository
import com.pulsefit.app.domain.repository.WorkoutRepository
import com.pulsefit.app.health.HealthConnectRepository
import com.pulsefit.app.util.CalorieCalculator
import com.pulsefit.app.util.MaxHrCalibrator
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
        zoneTime: Map<HeartRateZone, Long>,
        hrrScore: Int? = null
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
            estimatedCalories = estimatedCalories,
            hrrScore = hrrScore
        )
        workoutRepository.updateWorkout(updatedWorkout)

        // Update user stats
        userRepository.incrementWorkoutCount(burnPoints, now.toEpochMilli())

        // Self-calibrating MaxHR
        if (profile != null && maxHr > 0) {
            val ageFormulaMax = 220 - profile.age
            val newCalibratedMax = MaxHrCalibrator.calibrate(
                observedMaxHr = maxHr,
                currentCalibratedMax = profile.calibratedMaxHr,
                ageFormulaMax = ageFormulaMax,
                workoutCount = profile.totalWorkouts + 1
            )
            if (newCalibratedMax != null && newCalibratedMax != profile.calibratedMaxHr) {
                val updatedProfile = profile.copy(calibratedMaxHr = newCalibratedMax)
                userRepository.saveUserProfile(updatedProfile)
            }
        }

        // Sync to Health Connect
        healthConnectRepository.writeWorkout(updatedWorkout, readings)
    }
}
