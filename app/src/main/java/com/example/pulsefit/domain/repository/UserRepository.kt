package com.example.pulsefit.domain.repository

import com.example.pulsefit.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun getUserProfileOnce(): UserProfile?
    suspend fun saveUserProfile(profile: UserProfile)
    suspend fun updateXp(xp: Long, newLevel: Int)
    suspend fun updateStreak(streak: Int, shieldUsed: Boolean)
    suspend fun incrementWorkoutCount(burnPoints: Int, workoutTime: Long)
}
