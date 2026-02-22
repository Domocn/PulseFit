package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pulsefit.app.data.local.entity.NotificationPreferencesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationPreferencesDao {
    @Query("SELECT * FROM notification_preferences WHERE id = 1")
    fun getPreferences(): Flow<NotificationPreferencesEntity?>

    @Query("SELECT * FROM notification_preferences WHERE id = 1")
    suspend fun getPreferencesOnce(): NotificationPreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(prefs: NotificationPreferencesEntity)
}
