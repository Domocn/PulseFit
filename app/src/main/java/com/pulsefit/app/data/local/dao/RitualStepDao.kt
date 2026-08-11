package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pulsefit.app.data.local.entity.RitualStepEntity
import com.pulsefit.app.data.model.RitualType
import kotlinx.coroutines.flow.Flow

@Dao
interface RitualStepDao {
    @Query("SELECT * FROM ritual_steps WHERE ritualType = :type ORDER BY sortOrder ASC")
    fun getByType(type: RitualType): Flow<List<RitualStepEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(steps: List<RitualStepEntity>)

    @Update
    suspend fun update(step: RitualStepEntity)

    @Query("DELETE FROM ritual_steps WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM ritual_steps WHERE ritualType = :type")
    suspend fun deleteAll(type: RitualType)
}
