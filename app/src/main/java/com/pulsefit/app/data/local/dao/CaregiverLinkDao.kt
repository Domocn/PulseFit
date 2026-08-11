package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pulsefit.app.data.local.entity.CaregiverLinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaregiverLinkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(link: CaregiverLinkEntity): Long

    @Query("SELECT * FROM caregiver_links ORDER BY linkedAt DESC")
    fun getAll(): Flow<List<CaregiverLinkEntity>>

    @Query("DELETE FROM caregiver_links WHERE id = :id")
    suspend fun deleteById(id: Long)
}
