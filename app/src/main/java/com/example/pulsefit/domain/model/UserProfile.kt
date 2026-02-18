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
    val onboardingComplete: Boolean = false
)
