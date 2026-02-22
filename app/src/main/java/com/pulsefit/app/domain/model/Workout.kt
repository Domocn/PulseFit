package com.pulsefit.app.domain.model

import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.data.model.WorkoutType
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
    val estimatedCalories: Int? = null,
    val notes: String? = null,
    val templateId: String? = null
)
