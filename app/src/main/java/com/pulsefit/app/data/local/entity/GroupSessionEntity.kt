package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_sessions")
data class GroupSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val hostUserId: String,
    val hostName: String = "",
    val scheduledTime: Long,
    val workoutTemplateJson: String = "",
    val templateName: String = "",
    val status: String = "SCHEDULED",
    val maxParticipants: Int = 10,
    val participantCount: Int = 0,
    val created: Long = System.currentTimeMillis(),
    val notes: String? = null
)

@Entity(tableName = "group_session_participants")
data class GroupSessionParticipantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val userId: String,
    val userName: String = "",
    val status: String = "JOINED",
    val joinedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val workoutId: Long? = null,
    val totalVolumeKg: Float? = null,
    val totalSets: Int? = null
)
