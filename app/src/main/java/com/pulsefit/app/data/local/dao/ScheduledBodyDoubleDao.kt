package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pulsefit.app.data.local.entity.ScheduledBodyDoubleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduledBodyDoubleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: ScheduledBodyDoubleEntity): Long

    @Query("SELECT * FROM scheduled_body_doubles WHERE scheduledAt >= :now ORDER BY scheduledAt ASC")
    fun getUpcoming(now: Long): Flow<List<ScheduledBodyDoubleEntity>>

    @Query("DELETE FROM scheduled_body_doubles WHERE id = :id")
    suspend fun deleteById(id: Long)
}
