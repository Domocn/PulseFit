package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pulsefit.app.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insert(workout: WorkoutEntity): Long

    @Update
    suspend fun update(workout: WorkoutEntity)

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getById(id: Long): WorkoutEntity?

    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<WorkoutEntity?>

    @Query("SELECT * FROM workouts ORDER BY startTime DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE startTime >= :startOfDay AND startTime < :endOfDay ORDER BY startTime DESC")
    fun getWorkoutsForDay(startOfDay: Long, endOfDay: Long): Flow<List<WorkoutEntity>>

    @Query("SELECT COALESCE(SUM(burnPoints), 0) FROM workouts WHERE startTime >= :startOfDay AND startTime < :endOfDay")
    fun getTodayBurnPoints(startOfDay: Long, endOfDay: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(burnPoints), 0) FROM workouts")
    suspend fun getTotalBurnPoints(): Long

    @Query("SELECT * FROM workouts WHERE startTime >= :startTime AND startTime < :endTime ORDER BY startTime DESC")
    suspend fun getWorkoutsInDateRange(startTime: Long, endTime: Long): List<WorkoutEntity>

    @Query("SELECT * FROM workouts WHERE endTime IS NOT NULL ORDER BY startTime DESC")
    suspend fun getCompletedWorkouts(): List<WorkoutEntity>

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteWorkout(id: Long)

    @Query("SELECT COUNT(*) FROM workouts WHERE endTime IS NOT NULL")
    suspend fun getCompletedWorkoutCount(): Int

    @Query("SELECT DISTINCT startTime / 86400000 as day FROM workouts WHERE endTime IS NOT NULL ORDER BY day DESC")
    suspend fun getWorkoutDays(): List<Long>
}
