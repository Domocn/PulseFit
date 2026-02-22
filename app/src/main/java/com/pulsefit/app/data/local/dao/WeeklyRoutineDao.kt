package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pulsefit.app.data.local.entity.WeeklyRoutineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyRoutineDao {
    @Query("SELECT * FROM weekly_routines ORDER BY dayOfWeek, timeHour, timeMinute")
    fun getAllRoutines(): Flow<List<WeeklyRoutineEntity>>

    @Query("SELECT * FROM weekly_routines WHERE dayOfWeek = :dayOfWeek AND enabled = 1 ORDER BY timeHour, timeMinute")
    fun getRoutinesForDay(dayOfWeek: Int): Flow<List<WeeklyRoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(routine: WeeklyRoutineEntity): Long

    @Delete
    suspend fun delete(routine: WeeklyRoutineEntity)

    @Query("DELETE FROM weekly_routines WHERE id = :id")
    suspend fun deleteById(id: Long)
}
