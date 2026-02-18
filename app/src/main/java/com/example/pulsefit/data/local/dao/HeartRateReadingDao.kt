package com.example.pulsefit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pulsefit.data.local.entity.HeartRateReadingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HeartRateReadingDao {
    @Insert
    suspend fun insert(reading: HeartRateReadingEntity)

    @Insert
    suspend fun insertAll(readings: List<HeartRateReadingEntity>)

    @Query("SELECT * FROM heart_rate_readings WHERE workoutId = :workoutId ORDER BY timestamp ASC")
    fun getReadingsForWorkout(workoutId: Long): Flow<List<HeartRateReadingEntity>>

    @Query("SELECT * FROM heart_rate_readings WHERE workoutId = :workoutId ORDER BY timestamp ASC")
    suspend fun getReadingsForWorkoutOnce(workoutId: Long): List<HeartRateReadingEntity>
}
