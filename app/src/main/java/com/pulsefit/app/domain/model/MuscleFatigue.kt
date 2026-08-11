package com.pulsefit.app.domain.model

data class MuscleFatigue(
    val muscleGroup: String,
    val fatigueScore: Float = 0f,
    val lastTrainedAt: Long? = null,
    val lastVolumeKg: Float = 0f,
    val recoveryHoursNeeded: Int = 48
)
