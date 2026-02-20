package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pulsefit.app.data.model.QuestType

@Entity(tableName = "daily_quests")
data class DailyQuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long, // start of day millis
    val questType: QuestType,
    val title: String,
    val description: String,
    val targetValue: Int,
    val currentValue: Int = 0,
    val difficulty: Int = 1, // 1=Easy, 2=Medium, 3=Hard
    val xpReward: Int = 0,
    val completed: Boolean = false
)
