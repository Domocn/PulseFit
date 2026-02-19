package com.example.pulsefit.domain.model

import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.data.model.WorkoutType
import java.time.Instant

data class Workout(
    val id: Long = 0,
    val type: WorkoutType = WorkoutType.QUICK_START,
    val startTime: Instant,
    val endTime: Instant? = null,
    val durationSeconds: Int = 0,
    val burnPoints: Int = 0,
    val averageHeartRate: Int = 0,
    val maxHeartRate: Int = 0,
    val zoneTime: Map<HeartRateZone, Long> = emptyMap(),
    val xpEarned: Int = 0,
    val isQuickStart: Boolean = false,
    val isJustFiveMin: Boolean = false,
    val estimatedCalories: Int? = null
)
