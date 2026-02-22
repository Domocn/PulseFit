package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.data.model.TreadMode

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
    val streakShieldUsedThisWeek: Boolean = false,
    val customZoneThresholds: String? = null,
    val units: String = "metric",
    val streakShieldsOwned: Int = 0,
    val ownedItems: String = "[]",
    val biologicalSex: String = "male",
    val firebaseUid: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val profileVisibility: String = "friends",
    val treadMode: TreadMode = TreadMode.RUNNER,
    val equipmentProfileJson: String? = null
)
