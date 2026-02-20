package com.pulsefit.app.data.repository

import com.pulsefit.app.data.local.dao.UserProfileDao
import com.pulsefit.app.data.local.entity.UserProfileEntity
import com.pulsefit.app.domain.model.UserProfile
import com.pulsefit.app.domain.repository.UserRepository
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
        streakShieldUsedThisWeek = streakShieldUsedThisWeek,
        customZoneThresholds = customZoneThresholds,
        units = units,
        streakShieldsOwned = streakShieldsOwned,
        ownedItems = ownedItems,
        biologicalSex = biologicalSex,
        firebaseUid = firebaseUid,
        displayName = displayName,
        photoUrl = photoUrl,
        profileVisibility = profileVisibility
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
        streakShieldUsedThisWeek = streakShieldUsedThisWeek,
        customZoneThresholds = customZoneThresholds,
        units = units,
        streakShieldsOwned = streakShieldsOwned,
        ownedItems = ownedItems,
        biologicalSex = biologicalSex,
        firebaseUid = firebaseUid,
        displayName = displayName,
        photoUrl = photoUrl,
        profileVisibility = profileVisibility
    )
}
