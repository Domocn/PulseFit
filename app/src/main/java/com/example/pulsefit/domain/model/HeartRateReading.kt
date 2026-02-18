package com.example.pulsefit.domain.model

import com.example.pulsefit.data.model.HeartRateZone
import java.time.Instant

data class HeartRateReading(
    val id: Long = 0,
    val workoutId: Long,
    val timestamp: Instant,
    val heartRate: Int,
    val zone: HeartRateZone
)
