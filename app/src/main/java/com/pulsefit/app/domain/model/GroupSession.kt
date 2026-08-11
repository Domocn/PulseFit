package com.pulsefit.app.domain.model

import java.time.Instant

data class GroupSession(
    val id: Long = 0,
    val name: String,
    val hostUserId: String,
    val hostName: String = "",
    val scheduledTime: Instant,
    val workoutTemplateJson: String = "",
    val templateName: String = "",
    val status: GroupSessionStatus = GroupSessionStatus.SCHEDULED,
    val maxParticipants: Int = 10,
    val participantCount: Int = 0,
    val created: Instant = Instant.now(),
    val notes: String? = null
)

enum class GroupSessionStatus(val label: String) {
    SCHEDULED("Scheduled"),
    LIVE("Live Now"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

data class GroupSessionParticipant(
    val id: Long = 0,
    val sessionId: Long,
    val userId: String,
    val userName: String = "",
    val status: ParticipantStatus = ParticipantStatus.JOINED,
    val joinedAt: Instant = Instant.now(),
    val completedAt: Instant? = null,
    val workoutId: Long? = null,
    val totalVolumeKg: Float? = null,
    val totalSets: Int? = null
)

enum class ParticipantStatus(val label: String) {
    JOINED("Joined"),
    IN_PROGRESS("Working Out"),
    COMPLETED("Completed"),
    SKIPPED("Skipped")
}
