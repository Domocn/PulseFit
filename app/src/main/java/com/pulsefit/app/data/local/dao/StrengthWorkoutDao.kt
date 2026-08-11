package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pulsefit.app.data.local.entity.StrengthWorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StrengthWorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: StrengthWorkoutEntity): Long

    @Update
    suspend fun update(entity: StrengthWorkoutEntity)

    @Delete
    suspend fun delete(entity: StrengthWorkoutEntity)

    @Query("SELECT * FROM strength_workouts WHERE id = :id")
    suspend fun getById(id: Long): StrengthWorkoutEntity?

    @Query("SELECT * FROM strength_workouts WHERE workoutId = :workoutId")
    suspend fun getByWorkoutId(workoutId: Long): StrengthWorkoutEntity?

    @Query("SELECT * FROM strength_workouts WHERE workoutId = :workoutId")
    fun getByWorkoutIdFlow(workoutId: Long): Flow<StrengthWorkoutEntity?>

    @Query("SELECT * FROM strength_workouts ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<StrengthWorkoutEntity>>

    @Query("SELECT * FROM strength_workouts ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 20): List<StrengthWorkoutEntity>

    @Query("DELETE FROM strength_workouts WHERE id = :id")
    suspend fun deleteById(id: Long)
}
