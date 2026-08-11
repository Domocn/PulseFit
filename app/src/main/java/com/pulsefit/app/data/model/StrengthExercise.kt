package com.pulsefit.app.data.model

/**
 * Core data models for strength training exercises.
 * Parallels the existing cardio-focused Exercise model but adds
 * muscle groups, equipment, sets/reps/weight tracking, and RPE.
 */

enum class MuscleGroup(val label: String, val bodyRegion: BodyRegion) {
    CHEST("Chest", BodyRegion.UPPER),
    BACK("Back", BodyRegion.UPPER),
    SHOULDERS("Shoulders", BodyRegion.UPPER),
    BICEPS("Biceps", BodyRegion.UPPER),
    TRICEPS("Triceps", BodyRegion.UPPER),
    FOREARMS("Forearms", BodyRegion.UPPER),
    QUADS("Quads", BodyRegion.LOWER),
    HAMSTRINGS("Hamstrings", BodyRegion.LOWER),
    GLUTES("Glutes", BodyRegion.LOWER),
    CALVES("Calves", BodyRegion.LOWER),
    CORE("Core", BodyRegion.CORE),
    LOWER_BACK("Lower Back", BodyRegion.CORE),
    FULL_BODY("Full Body", BodyRegion.FULL)
}

enum class BodyRegion(val label: String) {
    UPPER("Upper Body"),
    LOWER("Lower Body"),
    CORE("Core"),
    FULL("Full Body")
}

enum class ExerciseCategory(val label: String) {
    STRENGTH("Strength"),
    CARDIO("Cardio"),
    STRETCHING("Stretching"),
    WARMUP("Warm-Up"),
    COOLDOWN("Cool-Down")
}

data class StrengthExercise(
    val id: String,
    val name: String,
    val primaryMuscleGroup: MuscleGroup,
    val secondaryMuscleGroups: List<MuscleGroup> = emptyList(),
    val equipment: List<Equipment> = emptyList(),
    val difficulty: Int = 1, // 1-5
    val instructions: String,
    val formTips: List<String> = emptyList(),
    val gifAsset: String? = null,
    val videoUrl: String? = null,
    val isBodyweight: Boolean = false,
    val category: ExerciseCategory = ExerciseCategory.STRENGTH
)
