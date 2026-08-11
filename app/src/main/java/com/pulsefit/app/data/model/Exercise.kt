package com.pulsefit.app.data.model

data class Exercise(
    val id: String,
    val name: String,
    val description: String,
    val station: ExerciseStation,
    val muscleGroups: List<String>,
    val lottieAsset: String?,
    val defaultDurationSeconds: Int,
    val formTips: List<String> = emptyList()
)

enum class ExerciseStation(val label: String) {
    TREAD("Tread"), ROW("Row"), FLOOR("Floor")
}
