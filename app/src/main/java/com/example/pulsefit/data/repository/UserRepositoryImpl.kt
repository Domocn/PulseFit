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

    override suspend fun updateXp(xp: Long, newLevel: Int) {
        userProfileDao.updateXp(xp, newLevel)
    }

    override suspend fun updateStreak(streak: Int, shieldUsed: Boolean) {
        userProfileDao.updateStreak(streak, shieldUsed)
    }

    override suspend fun incrementWorkoutCount(burnPoints: Int, workoutTime: Long) {
        userProfileDao.incrementWorkoutCount(burnPoints, workoutTime)
    }

    private fun UserProfileEntity.toDomain() = UserProfile(
        name = name,
        age = age,
        maxHeartRate = maxHeartRate,
        weight = weight,
        height = height,
        ndProfile = ndProfile,
        dailyTarget = dailyTarget,
        onboardingComplete = onboardingComplete,
        restingHeartRate = restingHeartRate,
        xpLevel = xpLevel,
        totalXp = totalXp,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        totalBurnPoints = totalBurnPoints,
        totalWorkouts = totalWorkouts,
        createdAt = createdAt,
        lastWorkoutAt = lastWorkoutAt,
        streakShieldUsedThisWeek = streakShieldUsedThisWeek
    )

    private fun UserProfile.toEntity() = UserProfileEntity(
        name = name,
        age = age,
        maxHeartRate = maxHeartRate,
        weight = weight,
        height = height,
        ndProfile = ndProfile,
        dailyTarget = dailyTarget,
        onboardingComplete = onboardingComplete,
        restingHeartRate = restingHeartRate,
        xpLevel = xpLevel,
        totalXp = totalXp,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        totalBurnPoints = totalBurnPoints,
        totalWorkouts = totalWorkouts,
        createdAt = createdAt,
        lastWorkoutAt = lastWorkoutAt,
        streakShieldUsedThisWeek = streakShieldUsedThisWeek
    )
}
