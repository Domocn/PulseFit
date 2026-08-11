package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Per-exercise 1RM (one-rep max) history.
 * Calculated via Brzycki formula: weight × (36 / (37 - reps))
 * from the best recent set for each exercise.
 */
@Entity(
    tableName = "one_rm_history",
    indices = [
        Index("exerciseId"),
        Index("timestamp")
    ]
)
data class OneRmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: String,
    val exerciseName: String,
    val estimatedOneRmKg: Float,
    val basedOnWeightKg: Float,
    val basedOnReps: Int,
    val formula: String = "Brzycki",  // Brzycki or Epley
    val timestamp: Long = System.currentTimeMillis()
)
