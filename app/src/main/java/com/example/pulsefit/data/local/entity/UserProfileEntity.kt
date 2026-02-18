package com.example.pulsefit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pulsefit.data.model.NdProfile

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val age: Int,
    val maxHeartRate: Int,
    val weight: Float? = null,
    val height: Float? = null,
    val ndProfile: NdProfile = NdProfile.STANDARD,
    val dailyTarget: Int = 12,
    val onboardingComplete: Boolean = false
)
