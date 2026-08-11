package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pulsefit.app.data.local.entity.ExerciseLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ExerciseLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ExerciseLogEntity>)

    @Query("SELECT * FROM exercise_logs WHERE workoutId = :workoutId ORDER BY id")
    suspend fun getByWorkoutId(workoutId: Long): List<ExerciseLogEntity>

    @Query("SELECT * FROM exercise_logs WHERE exerciseId = :exerciseId ORDER BY timestamp DESC")
    suspend fun getByExerciseId(exerciseId: String): List<ExerciseLogEntity>

    @Query("SELECT * FROM exercise_logs WHERE exerciseId = :exerciseId ORDER BY timestamp DESC")
    fun getByExerciseIdFlow(exerciseId: String): Flow<List<ExerciseLogEntity>>

    @Query("SELECT * FROM exercise_logs WHERE exerciseId = :exerciseId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestForExercise(exerciseId: String): ExerciseLogEntity?

    @Query("SELECT * FROM exercise_logs ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 50): List<ExerciseLogEntity>

    @Query("SELECT * FROM exercise_logs WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    suspend fun getInDateRange(startTime: Long, endTime: Long): List<ExerciseLogEntity>

    @Query("SELECT MAX(maxWeightKg) FROM exercise_logs WHERE exerciseId = :exerciseId")
    suspend fun getPersonalRecordWeight(exerciseId: String): Float?

    @Query("SELECT MAX(totalVolumeKg) FROM exercise_logs WHERE exerciseId = :exerciseId")
    suspend fun getPersonalRecordVolume(exerciseId: String): Float?

    @Query("SELECT DISTINCT exerciseId FROM exercise_logs ORDER BY exerciseId")
    suspend fun getDistinctExerciseIds(): List<String>

    @Query("SELECT * FROM exercise_logs WHERE primaryMuscleGroup = :muscleGroup AND timestamp > :since ORDER BY timestamp DESC")
    suspend fun getByMuscleGroupSince(muscleGroup: String, since: Long): List<ExerciseLogEntity>

    @Query("DELETE FROM exercise_logs WHERE workoutId = :workoutId")
    suspend fun deleteByWorkoutId(workoutId: Long)
}
