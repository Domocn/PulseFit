package com.pulsefit.app.domain.model

import java.time.Instant

data class ExerciseLog(
    val id: Long = 0,
    val workoutId: Long,
    val exerciseId: String,
    val exerciseName: String,
    val primaryMuscleGroup: String,
    val setsCompleted: Int = 0,
    val setsPlanned: Int = 0,
    val maxWeightKg: Float? = null,
    val totalVolumeKg: Float = 0f,
    val bestSetReps: Int? = null,
    val bestSetWeightKg: Float? = null,
    val averageRpe: Float? = null,
    val timestamp: Instant = Instant.now()
)
