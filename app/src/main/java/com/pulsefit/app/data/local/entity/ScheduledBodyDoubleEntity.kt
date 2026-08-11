package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduled_body_doubles")
data class ScheduledBodyDoubleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val firestoreSessionId: String = "",
    val scheduledAt: Long,
    val durationMinutes: Int = 30,
    val title: String = "",
    val createdByUid: String = "",
    val isJoined: Boolean = false
)
