package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pulsefit.app.data.local.entity.BodyMeasurementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasurementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BodyMeasurementEntity): Long

    @Query("SELECT * FROM body_measurements ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<BodyMeasurementEntity>>

    @Query("SELECT * FROM body_measurements ORDER BY timestamp DESC")
    suspend fun getAll(): List<BodyMeasurementEntity>

    @Query("SELECT * FROM body_measurements ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): BodyMeasurementEntity?

    @Query("SELECT * FROM body_measurements WHERE id = :id")
    suspend fun getById(id: Long): BodyMeasurementEntity?

    @Query("SELECT * FROM body_measurements WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp ASC")
    suspend fun getInDateRange(startTime: Long, endTime: Long): List<BodyMeasurementEntity>

    @Query("SELECT * FROM body_measurements WHERE weightKg IS NOT NULL ORDER BY timestamp ASC")
    suspend fun getWeightHistory(): List<BodyMeasurementEntity>

    @Query("SELECT * FROM body_measurements WHERE bodyFatPercent IS NOT NULL ORDER BY timestamp ASC")
    suspend fun getBodyFatHistory(): List<BodyMeasurementEntity>

    @Query("DELETE FROM body_measurements WHERE id = :id")
    suspend fun deleteById(id: Long)
}
