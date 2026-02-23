package com.pulsefit.app.data.repository

import com.pulsefit.app.data.local.dao.RewardAdTrackingDao
import com.pulsefit.app.data.local.dao.UserProfileDao
import com.pulsefit.app.data.local.entity.RewardAdTrackingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardCoinRepository @Inject constructor(
    private val rewardAdTrackingDao: RewardAdTrackingDao,
    private val userProfileDao: UserProfileDao
) {
    companion object {
        const val DAILY_AD_LIMIT = 5
        const val COINS_PER_AD = 10
    }

    fun adsRemainingToday(): Flow<Int> {
        return rewardAdTrackingDao.getTracking().map { tracking ->
            val today = LocalDate.now().toString()
            if (tracking == null || tracking.lastResetDate != today) {
                DAILY_AD_LIMIT
            } else {
                (DAILY_AD_LIMIT - tracking.adsWatchedToday).coerceAtLeast(0)
            }
        }
    }

    suspend fun canWatchAd(): Boolean {
        val tracking = rewardAdTrackingDao.getTrackingOnce()
        val today = LocalDate.now().toString()
        if (tracking == null || tracking.lastResetDate != today) return true
        return tracking.adsWatchedToday < DAILY_AD_LIMIT
    }

    suspend fun recordAdWatched() {
        val today = LocalDate.now().toString()
        val existing = rewardAdTrackingDao.getTrackingOnce()

        val updated = if (existing == null || existing.lastResetDate != today) {
            RewardAdTrackingEntity(
                adsWatchedToday = 1,
                lastAdWatchedAt = System.currentTimeMillis(),
                lastResetDate = today,
                totalAdsWatched = (existing?.totalAdsWatched ?: 0) + 1
            )
        } else {
            existing.copy(
                adsWatchedToday = existing.adsWatchedToday + 1,
                lastAdWatchedAt = System.currentTimeMillis(),
                totalAdsWatched = existing.totalAdsWatched + 1
            )
        }

        rewardAdTrackingDao.insertOrUpdate(updated)
        userProfileDao.addRewardCoins(COINS_PER_AD)
    }

    suspend fun spendCoins(amount: Int): Boolean {
        val profile = userProfileDao.getUserProfileOnce() ?: return false
        if (profile.rewardCoins < amount) return false
        userProfileDao.spendRewardCoins(amount)
        return true
    }
}
