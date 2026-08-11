package com.pulsefit.app.domain.model

import java.time.Instant

data class PersonalRecord(
    val id: Long = 0,
    val exerciseId: String,
    val exerciseName: String,
    val estimatedOneRmKg: Float,
    val basedOnWeightKg: Float,
    val basedOnReps: Int,
    val formula: String = "Brzycki",
    val timestamp: Instant = Instant.now()
)
