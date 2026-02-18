package com.example.pulsefit.domain.repository

import com.example.pulsefit.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun getUserProfileOnce(): UserProfile?
    suspend fun saveUserProfile(profile: UserProfile)
}
