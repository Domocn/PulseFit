package com.example.pulsefit.domain.model

import com.example.pulsefit.data.model.NdProfile

data class UserProfile(
    val name: String,
    val age: Int,
    val maxHeartRate: Int = 220 - age,
    val weight: Float? = null,
    val height: Float? = null,
    val ndProfile: NdProfile = NdProfile.STANDARD,
    val dailyTarget: Int = 12,
    val onboardingComplete: Boolean = false,
    val restingHeartRate: Int? = null,
    val xpLevel: Int = 1,
    val totalXp: Long = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalBurnPoints: Long = 0,
    val totalWorkouts: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastWorkoutAt: Long? = null,
    val streakShieldUsedThisWeek: Boolean = false
)
