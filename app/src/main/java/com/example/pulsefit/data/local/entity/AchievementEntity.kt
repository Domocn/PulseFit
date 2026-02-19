package com.example.pulsefit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconName: String = "star",
    val unlockedAt: Long? = null,
    val category: String = "general"
)
