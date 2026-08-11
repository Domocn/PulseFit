package com.pulsefit.app.domain.repository

import com.pulsefit.app.domain.model.GroupSession
import com.pulsefit.app.domain.model.GroupSessionParticipant
import kotlinx.coroutines.flow.Flow

interface GroupSessionRepository {
    suspend fun createSession(session: GroupSession): Long
    fun getUpcomingSessions(): Flow<List<GroupSession>>
    fun getSessionsByHost(userId: String): Flow<List<GroupSession>>
    suspend fun getSessionById(id: Long): GroupSession?
    suspend fun updateStatus(id: Long, status: String)
    suspend fun deleteSession(id: Long)

    suspend fun joinSession(sessionId: Long, userId: String, userName: String): Long
    suspend fun leaveSession(sessionId: Long, userId: String)
    fun getParticipants(sessionId: Long): Flow<List<GroupSessionParticipant>>
    suspend fun getParticipant(sessionId: Long, userId: String): GroupSessionParticipant?
    fun getSessionsByParticipant(userId: String): Flow<List<GroupSessionParticipant>>
    suspend fun updateParticipantResult(
        participantId: Long, status: String, completedAt: Long?,
        workoutId: Long?, volume: Float?, sets: Int?
    )
}
