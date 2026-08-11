package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Per-exercise historical log entry.
 * One row per exercise performed in a workout.
 * Enables personal records, progression tracking, and volume analysis.
 */
@Entity(
    tableName = "exercise_logs",
    indices = [
        Index("workoutId"),
        Index("exerciseId"),
        Index("timestamp")
    ]
)
data class ExerciseLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val exerciseId: String,
    val exerciseName: String,
    val primaryMuscleGroup: String,
    val setsCompleted: Int = 0,
    val setsPlanned: Int = 0,
    val maxWeightKg: Float? = null,
    val totalVolumeKg: Float = 0f,   // sum of (reps × weight) across all sets
    val bestSetReps: Int? = null,
    val bestSetWeightKg: Float? = null,
    val averageRpe: Float? = null,
    val timestamp: Long = System.currentTimeMillis()
)
