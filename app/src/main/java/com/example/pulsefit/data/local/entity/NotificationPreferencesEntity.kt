package com.example.pulsefit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_preferences")
data class NotificationPreferencesEntity(
    @PrimaryKey val id: Int = 1,
    val reminderHour: Int = 18,
    val reminderMinute: Int = 0,
    val reminderEnabled: Boolean = false,
    val streakAlertEnabled: Boolean = true,
    val weeklySummaryEnabled: Boolean = true
)
