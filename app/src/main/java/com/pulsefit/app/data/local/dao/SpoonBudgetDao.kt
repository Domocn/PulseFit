package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pulsefit.app.data.local.entity.SpoonBudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpoonBudgetDao {
    @Query("SELECT * FROM spoon_budget WHERE id = 1")
    fun getBudget(): Flow<SpoonBudgetEntity?>

    @Query("SELECT * FROM spoon_budget WHERE id = 1")
    suspend fun getBudgetOnce(): SpoonBudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(budget: SpoonBudgetEntity)
}
