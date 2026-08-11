package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spoon_budget")
data class SpoonBudgetEntity(
    @PrimaryKey val id: Int = 1,
    val dailySpoons: Int = 12,
    val usedSpoons: Float = 0f,
    val spoonResetAt: Long = 0L
)
