package com.example.pulsefit.data.repository

import com.example.pulsefit.data.local.dao.UserProfileDao
import com.example.pulsefit.data.local.entity.UserProfileEntity
import com.example.pulsefit.domain.model.UserProfile
import com.example.pulsefit.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
) : UserRepository {

    override fun getUserProfile(): Flow<UserProfile?> {
        return userProfileDao.getUserProfile().map { it?.toDomain() }
    }

    override suspend fun getUserProfileOnce(): UserProfile? {
        return userProfileDao.getUserProfileOnce()?.toDomain()
    }

    override suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdate(profile.toEntity())
    }

    private fun UserProfileEntity.toDomain() = UserProfile(
        name = name,
        age = age,
        maxHeartRate = maxHeartRate,
        weight = weight,
        height = height,
        ndProfile = ndProfile,
        dailyTarget = dailyTarget,
        onboardingComplete = onboardingComplete
    )

    private fun UserProfile.toEntity() = UserProfileEntity(
        name = name,
        age = age,
        maxHeartRate = maxHeartRate,
        weight = weight,
        height = height,
        ndProfile = ndProfile,
        dailyTarget = dailyTarget,
        onboardingComplete = onboardingComplete
    )
}
