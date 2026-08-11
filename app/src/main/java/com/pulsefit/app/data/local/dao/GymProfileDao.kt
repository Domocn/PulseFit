package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pulsefit.app.data.local.entity.GymProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GymProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: GymProfileEntity): Long

    @Update
    suspend fun update(profile: GymProfileEntity)

    @Delete
    suspend fun delete(profile: GymProfileEntity)

    @Query("SELECT * FROM gym_profiles ORDER BY isFavorite DESC, name ASC")
    fun getAll(): Flow<List<GymProfileEntity>>

    @Query("SELECT * FROM gym_profiles WHERE id = :id")
    suspend fun getById(id: Long): GymProfileEntity?

    @Query("SELECT * FROM gym_profiles WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavorites(): Flow<List<GymProfileEntity>>
}
