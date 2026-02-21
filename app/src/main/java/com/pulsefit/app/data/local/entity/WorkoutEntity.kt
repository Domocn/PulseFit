package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pulsefit.app.data.model.WorkoutType

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: WorkoutType = WorkoutType.QUICK_START,
    val startTime: Long,
    val endTime: Long? = null,
    val durationSeconds: Int = 0,
    val burnPoints: Int = 0,
    val averageHeartRate: Int = 0,
    val maxHeartRate: Int = 0,
    val zoneTimeJson: String = "{}",
    val xpEarned: Int = 0,
    val isQuickStart: Boolean = false,
    val isJustFiveMin: Boolean = false,
    val estimatedCalories: Int? = null,
    val notes: String? = null,
    val templateId: String? = null
)
