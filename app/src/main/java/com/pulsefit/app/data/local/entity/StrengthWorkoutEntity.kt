package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Persisted strength workout structure.
 * Links to the parent WorkoutEntity via workoutId foreign key.
 * The full exercise/set/reps/weight structure is stored as JSON
 * to avoid an explosion of relational tables.
 */
@Entity(
    tableName = "strength_workouts",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutId")]
)
data class StrengthWorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val name: String,
    val exercisesJson: String,       // JSON array of WorkoutExercise
    val warmupExercisesJson: String = "[]",
    val cooldownExercisesJson: String = "[]",
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
