package com.pulsefit.app.data.repository

import android.util.Log
import com.pulsefit.app.data.local.dao.UserProfileDao
import com.pulsefit.app.data.local.entity.UserProfileEntity
import com.pulsefit.app.data.remote.AuthRepository
import com.pulsefit.app.data.remote.CloudProfile
import com.pulsefit.app.data.remote.CloudProfileRepository
import com.pulsefit.app.domain.model.UserProfile
import com.pulsefit.app.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val cloudProfileRepository: CloudProfileRepository,
    private val authRepository: AuthRepository
) : UserRepository {

    private val syncScope = CoroutineScope(Dispatchers.IO)

    override fun getUserProfile(): Flow<UserProfile?> {
        return userProfileDao.getUserProfile().map { it?.toDomain() }
    }

    override suspend fun getUserProfileOnce(): UserProfile? {
        return userProfileDao.getUserProfileOnce()?.toDomain()
    }

    override suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdate(profile.toEntity())
        // Fire-and-forget push to Firestore
        if (authRepository.isAuthenticated) {
            syncScope.launch {
                try {
                    cloudProfileRepository.pushProfile(CloudProfile.fromUserProfile(profile))
                } catch (e: Exception) {
                    Log.w("UserRepositoryImpl", "Cloud sync failed", e)
                }
            }
        }
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

    override suspend fun restoreFromCloud(): UserProfile? {
        if (!authRepository.isAuthenticated) return null
        return try {
            val cloudProfile = cloudProfileRepository.pullProfile() ?: return null
            if (!cloudProfile.onboardingComplete) return null
            val uid = authRepository.currentUser?.uid
            val profile = with(CloudProfile.Companion) { cloudProfile.toDomainProfile(uid) }
            userProfileDao.insertOrUpdate(profile.toEntity())
            profile
        } catch (e: Exception) {
            Log.w("UserRepositoryImpl", "Cloud restore failed", e)
            null
        }
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
        profileVisibility = profileVisibility,
        treadMode = treadMode,
        equipmentProfileJson = equipmentProfileJson
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
        profileVisibility = profileVisibility,
        treadMode = treadMode,
        equipmentProfileJson = equipmentProfileJson
    )
}
