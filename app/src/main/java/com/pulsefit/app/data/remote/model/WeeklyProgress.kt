package com.pulsefit.app.data.remote.model

data class WeeklyProgress(
    val myCount: Int = 0,
    val partnerCount: Int = 0,
    val weeklyGoal: Int = 3,
    val weekStart: Long = 0
)
