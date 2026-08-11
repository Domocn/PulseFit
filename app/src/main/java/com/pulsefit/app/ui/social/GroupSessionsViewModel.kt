package com.pulsefit.app.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.domain.model.GroupSession
import com.pulsefit.app.domain.model.GroupSessionParticipant
import com.pulsefit.app.domain.model.GroupSessionStatus
import com.pulsefit.app.domain.repository.GroupSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class GroupSessionsState(
    val upcomingSessions: List<GroupSession> = emptyList(),
    val mySessions: List<GroupSession> = emptyList(),
    val selectedSession: GroupSession? = null,
    val participants: List<GroupSessionParticipant> = emptyList(),
    val myParticipations: List<GroupSessionParticipant> = emptyList(),
    val isCreating: Boolean = false,
    val isJoining: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GroupSessionsViewModel @Inject constructor(
    private val repository: GroupSessionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GroupSessionsState())
    val state: StateFlow<GroupSessionsState> = _state

    // In a real app, this would come from auth
    private val currentUserId = "local_user"
    private val currentUserName = "You"

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            repository.getUpcomingSessions().collect { sessions ->
                _state.value = _state.value.copy(upcomingSessions = sessions)
            }
        }
        viewModelScope.launch {
            repository.getSessionsByHost(currentUserId).collect { sessions ->
                _state.value = _state.value.copy(mySessions = sessions)
            }
        }
        viewModelScope.launch {
            repository.getSessionsByParticipant(currentUserId).collect { parts ->
                _state.value = _state.value.copy(myParticipations = parts)
            }
        }
    }

    fun createSession(
        name: String,
        scheduledTime: Instant,
        templateName: String,
        workoutTemplateJson: String,
        maxParticipants: Int,
        notes: String?
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCreating = true, error = null)
            try {
                repository.createSession(
                    GroupSession(
                        name = name,
                        hostUserId = currentUserId,
                        hostName = currentUserName,
                        scheduledTime = scheduledTime,
                        workoutTemplateJson = workoutTemplateJson,
                        templateName = templateName,
                        maxParticipants = maxParticipants,
                        notes = notes
                    )
                )
                _state.value = _state.value.copy(isCreating = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isCreating = false,
                    error = e.message ?: "Failed to create session"
                )
            }
        }
    }

    fun joinSession(sessionId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isJoining = true, error = null)
            try {
                repository.joinSession(sessionId, currentUserId, currentUserName)
                _state.value = _state.value.copy(isJoining = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isJoining = false,
                    error = e.message ?: "Failed to join session"
                )
            }
        }
    }

    fun leaveSession(sessionId: Long) {
        viewModelScope.launch {
            try {
                repository.leaveSession(sessionId, currentUserId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to leave session"
                )
            }
        }
    }

    fun selectSession(session: GroupSession) {
        _state.value = _state.value.copy(selectedSession = session)
        viewModelScope.launch {
            repository.getParticipants(session.id).collect { participants ->
                _state.value = _state.value.copy(participants = participants)
            }
        }
    }

    fun cancelSession(sessionId: Long) {
        viewModelScope.launch {
            try {
                repository.updateStatus(sessionId, GroupSessionStatus.CANCELLED.name)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to cancel session"
                )
            }
        }
    }

    fun startSession(sessionId: Long) {
        viewModelScope.launch {
            try {
                repository.updateStatus(sessionId, GroupSessionStatus.LIVE.name)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to start session"
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
