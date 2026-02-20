package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pulsefit.app.data.local.entity.DailyQuestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyQuestDao {
    @Query("SELECT * FROM daily_quests WHERE date = :date ORDER BY difficulty")
    fun getQuestsForDate(date: Long): Flow<List<DailyQuestEntity>>

    @Query("SELECT * FROM daily_quests WHERE date = :date ORDER BY difficulty")
    suspend fun getQuestsForDateOnce(date: Long): List<DailyQuestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(quests: List<DailyQuestEntity>)

    @Update
    suspend fun update(quest: DailyQuestEntity)

    @Query("DELETE FROM daily_quests WHERE date < :beforeDate")
    suspend fun deleteOldQuests(beforeDate: Long)
}
