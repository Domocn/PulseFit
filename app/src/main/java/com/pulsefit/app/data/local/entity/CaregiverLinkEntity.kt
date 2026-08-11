package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "caregiver_links")
data class CaregiverLinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val caregiverUid: String,
    val linkedAt: Long = System.currentTimeMillis(),
    val permissions: String = "readiness,streaks"
)
