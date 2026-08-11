package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pulsefit.app.data.local.entity.MuscleFatigueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MuscleFatigueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: MuscleFatigueEntity)

    @Query("SELECT * FROM muscle_fatigue ORDER BY fatigueScore DESC")
    fun getAllFlow(): Flow<List<MuscleFatigueEntity>>

    @Query("SELECT * FROM muscle_fatigue ORDER BY fatigueScore DESC")
    suspend fun getAll(): List<MuscleFatigueEntity>

    @Query("SELECT * FROM muscle_fatigue WHERE muscleGroup = :muscleGroup")
    suspend fun getByMuscleGroup(muscleGroup: String): MuscleFatigueEntity?

    @Query("SELECT * FROM muscle_fatigue WHERE muscleGroup = :muscleGroup")
    fun getByMuscleGroupFlow(muscleGroup: String): Flow<MuscleFatigueEntity?>

    @Query("SELECT * FROM muscle_fatigue WHERE fatigueScore < :threshold AND lastTrainedAt IS NOT NULL ORDER BY fatigueScore ASC")
    suspend fun getReadyMuscles(threshold: Float = 30f): List<MuscleFatigueEntity>

    @Query("SELECT * FROM muscle_fatigue WHERE fatigueScore >= :threshold ORDER BY fatigueScore DESC")
    suspend fun getFatiguedMuscles(threshold: Float = 50f): List<MuscleFatigueEntity>

    @Query("DELETE FROM muscle_fatigue")
    suspend fun deleteAll()
}
