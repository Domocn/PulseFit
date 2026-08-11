package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Per-muscle-group fatigue tracking.
 * Fatigue score (0-100%) is calculated from recent workout volume
 * and time since last training session for that muscle group.
 * Higher score = more fatigued / needs more recovery.
 */
@Entity(
    tableName = "muscle_fatigue",
    indices = [Index("muscleGroup", unique = true)]
)
data class MuscleFatigueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val muscleGroup: String,          // MuscleGroup enum name
    val fatigueScore: Float = 0f,     // 0-100%, calculated
    val lastTrainedAt: Long? = null,
    val lastVolumeKg: Float = 0f,
    val recoveryHoursNeeded: Int = 48,
    val updatedAt: Long = System.currentTimeMillis()
)
