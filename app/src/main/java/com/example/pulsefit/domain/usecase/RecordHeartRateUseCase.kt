package com.example.pulsefit.domain.usecase

import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.domain.model.HeartRateReading
import com.example.pulsefit.domain.repository.WorkoutRepository
import java.time.Instant
import javax.inject.Inject

class RecordHeartRateUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    suspend operator fun invoke(workoutId: Long, heartRate: Int, zone: HeartRateZone) {
        workoutRepository.saveHeartRateReading(
            HeartRateReading(
                workoutId = workoutId,
                timestamp = Instant.now(),
                heartRate = heartRate,
                zone = zone
            )
        )
    }
}
