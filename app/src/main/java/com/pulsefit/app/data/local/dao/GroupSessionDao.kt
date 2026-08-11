package com.pulsefit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pulsefit.app.data.local.entity.GroupSessionEntity
import com.pulsefit.app.data.local.entity.GroupSessionParticipantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupSessionDao {

    // Sessions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GroupSessionEntity): Long

    @Query("SELECT * FROM group_sessions ORDER BY scheduledTime DESC")
    fun getAllSessionsFlow(): Flow<List<GroupSessionEntity>>

    @Query("SELECT * FROM group_sessions WHERE status = 'SCHEDULED' OR status = 'LIVE' ORDER BY scheduledTime ASC")
    fun getUpcomingSessionsFlow(): Flow<List<GroupSessionEntity>>

    @Query("SELECT * FROM group_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): GroupSessionEntity?

    @Query("SELECT * FROM group_sessions WHERE hostUserId = :userId ORDER BY scheduledTime DESC")
    fun getSessionsByHostFlow(userId: String): Flow<List<GroupSessionEntity>>

    @Query("UPDATE group_sessions SET status = :status WHERE id = :id")
    suspend fun updateSessionStatus(id: Long, status: String)

    @Query("UPDATE group_sessions SET participantCount = participantCount + 1 WHERE id = :id")
    suspend fun incrementParticipantCount(id: Long)

    @Query("DELETE FROM group_sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)

    // Participants
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: GroupSessionParticipantEntity): Long

    @Query("SELECT * FROM group_session_participants WHERE sessionId = :sessionId")
    fun getParticipantsFlow(sessionId: Long): Flow<List<GroupSessionParticipantEntity>>

    @Query("SELECT * FROM group_session_participants WHERE sessionId = :sessionId")
    suspend fun getParticipants(sessionId: Long): List<GroupSessionParticipantEntity>

    @Query("SELECT * FROM group_session_participants WHERE sessionId = :sessionId AND userId = :userId LIMIT 1")
    suspend fun getParticipant(sessionId: Long, userId: String): GroupSessionParticipantEntity?

    @Query("SELECT * FROM group_session_participants WHERE userId = :userId ORDER BY joinedAt DESC")
    fun getSessionsByParticipantFlow(userId: String): Flow<List<GroupSessionParticipantEntity>>

    @Query("UPDATE group_session_participants SET status = :status, completedAt = :completedAt, workoutId = :workoutId, totalVolumeKg = :volume, totalSets = :sets WHERE id = :id")
    suspend fun updateParticipantResult(
        id: Long,
        status: String,
        completedAt: Long?,
        workoutId: Long?,
        volume: Float?,
        sets: Int?
    )

    @Query("UPDATE group_session_participants SET status = :status WHERE id = :id")
    suspend fun updateParticipantStatus(id: Long, status: String)

    @Query("DELETE FROM group_session_participants WHERE sessionId = :sessionId AND userId = :userId")
    suspend fun removeParticipant(sessionId: Long, userId: String)
}
