package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pulsefit.app.data.local.entity.OneRmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OneRmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: OneRmEntity): Long

    @Query("SELECT * FROM one_rm_history WHERE exerciseId = :exerciseId ORDER BY timestamp DESC")
    suspend fun getByExerciseId(exerciseId: String): List<OneRmEntity>

    @Query("SELECT * FROM one_rm_history WHERE exerciseId = :exerciseId ORDER BY timestamp DESC")
    fun getByExerciseIdFlow(exerciseId: String): Flow<List<OneRmEntity>>

    @Query("SELECT * FROM one_rm_history WHERE exerciseId = :exerciseId ORDER BY estimatedOneRmKg DESC LIMIT 1")
    suspend fun getCurrentBest(exerciseId: String): OneRmEntity?

    @Query("SELECT * FROM one_rm_history ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<OneRmEntity>>

    @Query("SELECT * FROM one_rm_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 50): List<OneRmEntity>

    @Query("DELETE FROM one_rm_history WHERE exerciseId = :exerciseId")
    suspend fun deleteByExerciseId(exerciseId: String)
}
