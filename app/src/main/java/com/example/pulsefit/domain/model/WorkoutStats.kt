package com.example.pulsefit.domain.model

data class WorkoutStats(
    val totalWorkouts: Int = 0,
    val totalBurnPoints: Int = 0,
    val todayBurnPoints: Int = 0,
    val currentStreak: Int = 0
)
