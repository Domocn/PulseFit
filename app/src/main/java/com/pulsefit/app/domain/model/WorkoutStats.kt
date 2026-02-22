package com.pulsefit.app.domain.model

data class WorkoutStats(
    val totalWorkouts: Int = 0,
    val totalBurnPoints: Int = 0,
    val todayBurnPoints: Int = 0,
    val currentStreak: Int = 0,
    val totalDurationSeconds: Int = 0,
    val averageBurnPoints: Int = 0,
    val averageDurationSeconds: Int = 0
)
