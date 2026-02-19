package com.example.pulsefit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pulsefit.data.local.entity.SensoryPreferencesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SensoryPreferencesDao {
    @Query("SELECT * FROM sensory_preferences WHERE id = 1")
    fun getPreferences(): Flow<SensoryPreferencesEntity?>

    @Query("SELECT * FROM sensory_preferences WHERE id = 1")
    suspend fun getPreferencesOnce(): SensoryPreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(prefs: SensoryPreferencesEntity)
}
