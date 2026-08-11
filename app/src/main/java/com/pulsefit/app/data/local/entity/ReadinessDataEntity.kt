package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "readiness_data")
data class ReadinessDataEntity(
    @PrimaryKey val id: Int = 1,
    val lastSleepHours: Float? = null,
    val lastSleepQuality: String? = null,
    val restingHr: Int? = null,
    val lastHrrScore: Float? = null,
    val lastHrrAt: Long? = null,
    val readinessScore: Int? = null,
    val readinessCalculatedAt: Long? = null,
    val selfReportedEnergy: Int? = null,
    val selfReportedEnergyAt: Long? = null
)
