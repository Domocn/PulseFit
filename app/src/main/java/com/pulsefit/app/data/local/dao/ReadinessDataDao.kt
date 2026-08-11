package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pulsefit.app.data.local.entity.ReadinessDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadinessDataDao {
    @Query("SELECT * FROM readiness_data WHERE id = 1")
    fun getReadiness(): Flow<ReadinessDataEntity?>

    @Query("SELECT * FROM readiness_data WHERE id = 1")
    suspend fun getReadinessOnce(): ReadinessDataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: ReadinessDataEntity)
}
