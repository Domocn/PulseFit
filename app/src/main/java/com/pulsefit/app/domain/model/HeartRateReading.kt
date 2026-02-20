package com.pulsefit.app.domain.model

import com.pulsefit.app.data.model.HeartRateZone
import java.time.Instant

data class HeartRateReading(
    val id: Long = 0,
    val workoutId: Long,
    val timestamp: Instant,
    val heartRate: Int,
    val zone: HeartRateZone
)
