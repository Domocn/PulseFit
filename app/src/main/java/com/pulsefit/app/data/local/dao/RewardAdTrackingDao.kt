package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pulsefit.app.data.local.entity.RewardAdTrackingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardAdTrackingDao {
    @Query("SELECT * FROM reward_ad_tracking WHERE id = 1")
    fun getTracking(): Flow<RewardAdTrackingEntity?>

    @Query("SELECT * FROM reward_ad_tracking WHERE id = 1")
    suspend fun getTrackingOnce(): RewardAdTrackingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(tracking: RewardAdTrackingEntity)
}
