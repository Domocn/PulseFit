package com.pulsefit.app.data.model

/**
 * Data models for strength workout structure.
 * A StrengthWorkout contains exercises, each with multiple sets
 * tracking reps, weight, RPE, and completion status.
 */

data class StrengthWorkout(
    val id: String,
    val name: String,
    val exercises: List<WorkoutExercise>,
    val warmupExercises: List<WorkoutExercise> = emptyList(),
    val cooldownExercises: List<WorkoutExercise> = emptyList(),
    val notes: String? = null
)

data class WorkoutExercise(
    val exerciseId: String,
    val sets: List<ExerciseSet> = emptyList(),
    val restSeconds: Int = 90,
    val notes: String? = null,
    val supersetGroup: Int? = null  // non-null = part of a superset group
)

data class ExerciseSet(
    val reps: Int,
    val weightKg: Float? = null,
    val isWarmup: Boolean = false,
    val isDropSet: Boolean = false,
    val rpe: Int? = null,  // 1-10 RPE scale
    val completed: Boolean = false,
    val actualReps: Int? = null,
    val actualWeightKg: Float? = null
)
