package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reward_ad_tracking")
data class RewardAdTrackingEntity(
    @PrimaryKey val id: Int = 1,
    val adsWatchedToday: Int = 0,
    val lastAdWatchedAt: Long? = null,
    val lastResetDate: String = "",
    val totalAdsWatched: Int = 0
)
