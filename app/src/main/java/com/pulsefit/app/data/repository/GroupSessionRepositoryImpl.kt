package com.pulsefit.app.data.repository

import com.pulsefit.app.data.local.dao.GroupSessionDao
import com.pulsefit.app.data.local.entity.GroupSessionEntity
import com.pulsefit.app.data.local.entity.GroupSessionParticipantEntity
import com.pulsefit.app.domain.model.GroupSession
import com.pulsefit.app.domain.model.GroupSessionParticipant
import com.pulsefit.app.domain.model.GroupSessionStatus
import com.pulsefit.app.domain.model.ParticipantStatus
import com.pulsefit.app.domain.repository.GroupSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupSessionRepositoryImpl @Inject constructor(
    private val dao: GroupSessionDao
) : GroupSessionRepository {

    override suspend fun createSession(session: GroupSession): Long {
        return dao.insertSession(session.toEntity())
    }

    override fun getUpcomingSessions(): Flow<List<GroupSession>> {
        return dao.getUpcomingSessionsFlow().map { list -> list.map { it.toDomain() } }
    }

    override fun getSessionsByHost(userId: String): Flow<List<GroupSession>> {
        return dao.getSessionsByHostFlow(userId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getSessionById(id: Long): GroupSession? {
        return dao.getSessionById(id)?.toDomain()
    }

    override suspend fun updateStatus(id: Long, status: String) {
        dao.updateSessionStatus(id, status)
    }

    override suspend fun deleteSession(id: Long) {
        dao.deleteSession(id)
    }

    override suspend fun joinSession(sessionId: Long, userId: String, userName: String): Long {
        val existing = dao.getParticipant(sessionId, userId)
        if (existing != null) return existing.id
        dao.incrementParticipantCount(sessionId)
        return dao.insertParticipant(
            GroupSessionParticipantEntity(
                sessionId = sessionId,
                userId = userId,
                userName = userName
            )
        )
    }

    override suspend fun leaveSession(sessionId: Long, userId: String) {
        dao.removeParticipant(sessionId, userId)
    }

    override fun getParticipants(sessionId: Long): Flow<List<GroupSessionParticipant>> {
        return dao.getParticipantsFlow(sessionId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getParticipant(sessionId: Long, userId: String): GroupSessionParticipant? {
        return dao.getParticipant(sessionId, userId)?.toDomain()
    }

    override fun getSessionsByParticipant(userId: String): Flow<List<GroupSessionParticipant>> {
        return dao.getSessionsByParticipantFlow(userId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun updateParticipantResult(
        participantId: Long, status: String, completedAt: Long?,
        workoutId: Long?, volume: Float?, sets: Int?
    ) {
        dao.updateParticipantResult(participantId, status, completedAt, workoutId, volume, sets)
    }

    private fun GroupSessionEntity.toDomain() = GroupSession(
        id = id,
        name = name,
        hostUserId = hostUserId,
        hostName = hostName,
        scheduledTime = Instant.ofEpochMilli(scheduledTime),
        workoutTemplateJson = workoutTemplateJson,
        templateName = templateName,
        status = try { GroupSessionStatus.valueOf(status) } catch (_: Exception) { GroupSessionStatus.SCHEDULED },
        maxParticipants = maxParticipants,
        participantCount = participantCount,
        created = Instant.ofEpochMilli(created),
        notes = notes
    )

    private fun GroupSession.toEntity() = GroupSessionEntity(
        id = id,
        name = name,
        hostUserId = hostUserId,
        hostName = hostName,
        scheduledTime = scheduledTime.toEpochMilli(),
        workoutTemplateJson = workoutTemplateJson,
        templateName = templateName,
        status = status.name,
        maxParticipants = maxParticipants,
        participantCount = participantCount,
        created = created.toEpochMilli(),
        notes = notes
    )

    private fun GroupSessionParticipantEntity.toDomain() = GroupSessionParticipant(
        id = id,
        sessionId = sessionId,
        userId = userId,
        userName = userName,
        status = try { ParticipantStatus.valueOf(status) } catch (_: Exception) { ParticipantStatus.JOINED },
        joinedAt = Instant.ofEpochMilli(joinedAt),
        completedAt = completedAt?.let { Instant.ofEpochMilli(it) },
        workoutId = workoutId,
        totalVolumeKg = totalVolumeKg,
        totalSets = totalSets
    )
}
