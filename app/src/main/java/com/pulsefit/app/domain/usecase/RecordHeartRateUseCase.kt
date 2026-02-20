package com.pulsefit.app.domain.usecase

import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.domain.model.HeartRateReading
import com.pulsefit.app.domain.repository.WorkoutRepository
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
