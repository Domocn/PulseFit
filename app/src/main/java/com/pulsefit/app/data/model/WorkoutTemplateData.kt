package com.pulsefit.app.data.model

data class WorkoutTemplateData(
    val id: String,
    val name: String,
    val description: String,
    val durationMinutes: Int,
    val type: String,
    val category: TemplateCategory,
    val difficulty: Int,
    val phases: List<TemplatePhase>
)

data class TemplatePhase(
    val name: String,
    val durationMinutes: Int,
    val zoneName: String,
    val station: ExerciseStation?,
    val exercises: List<PhaseExercise>
)

data class PhaseExercise(
    val exerciseId: String,
    val durationSeconds: Int,
    val reps: Int? = null,
    val notes: String? = null
)

enum class TemplateCategory(val label: String) {
    STANDARD("Standard"),
    BEGINNER("Beginner"),
    ADVANCED("Advanced"),
    SPECIALTY("Specialty"),
    OTF_STYLE("Class Formats")
}
