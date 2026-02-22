package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_routines")
data class WeeklyRoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: Int, // 1=Monday .. 7=Sunday
    val timeHour: Int,
    val timeMinute: Int,
    val durationMinutes: Int = 30,
    val workoutType: String = "QUICK_START",
    val enabled: Boolean = true
)
