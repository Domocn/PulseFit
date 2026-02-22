package com.pulsefit.app.ui.group

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.remote.GroupChallengeRepository
import com.pulsefit.app.data.remote.GroupEventRepository
import com.pulsefit.app.data.remote.GroupRepository
import com.pulsefit.app.data.remote.model.ChallengeGoalType
import com.pulsefit.app.data.remote.model.GroupChallenge
import com.pulsefit.app.data.remote.model.GroupEvent
import com.pulsefit.app.data.remote.model.GroupMember
import com.pulsefit.app.data.remote.model.GroupWeeklyStats
import com.pulsefit.app.data.remote.model.WorkoutGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val groupRepository: GroupRepository,
    private val eventRepository: GroupEventRepository,
    private val challengeRepository: GroupChallengeRepository
) : ViewModel() {

    private val groupId: String = savedStateHandle["groupId"] ?: ""

    private val _group = MutableStateFlow<WorkoutGroup?>(null)
    val group: StateFlow<WorkoutGroup?> = _group

    private val _members = MutableStateFlow<List<GroupMember>>(emptyList())
    val members: StateFlow<List<GroupMember>> = _members

    private val _events = MutableStateFlow<List<GroupEvent>>(emptyList())
    val events: StateFlow<List<GroupEvent>> = _events

    private val _challenges = MutableStateFlow<List<GroupChallenge>>(emptyList())
    val challenges: StateFlow<List<GroupChallenge>> = _challenges

    private val _weeklyStats = MutableStateFlow<GroupWeeklyStats?>(null)
    val weeklyStats: StateFlow<GroupWeeklyStats?> = _weeklyStats

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    init {
        loadGroup()
        viewModelScope.launch {
            groupRepository.getMembers(groupId).collect { _members.value = it }
        }
        viewModelScope.launch {
            eventRepository.getUpcomingEvents(groupId).collect { _events.value = it }
        }
        viewModelScope.launch {
            challengeRepository.getActiveChallenges(groupId).collect { _challenges.value = it }
        }
        viewModelScope.launch {
            challengeRepository.getWeeklyStats(groupId).collect { _weeklyStats.value = it }
        }
    }

    private fun loadGroup() {
        viewModelScope.launch {
            val g = groupRepository.getGroupById(groupId)
            _group.value = g
            _isAdmin.value = g != null && groupRepository.isCurrentUserAdmin(g)
        }
    }

    fun createEvent(title: String, scheduledAt: Long, durationMinutes: Int) {
        viewModelScope.launch {
            val event = GroupEvent(
                title = title,
                scheduledAt = scheduledAt,
                durationMinutes = durationMinutes
            )
            eventRepository.createEvent(groupId, event)
        }
    }

    fun rsvpEvent(eventId: String) {
        viewModelScope.launch { eventRepository.rsvpEvent(groupId, eventId) }
    }

    fun createChallenge(title: String, goalType: ChallengeGoalType, goalTarget: Int, durationDays: Int) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val challenge = GroupChallenge(
                title = title,
                goalType = goalType,
                goalTarget = goalTarget,
                startDate = now,
                endDate = now + durationDays * 86400000L
            )
            challengeRepository.createChallenge(groupId, challenge)
        }
    }

    fun removeMember(uid: String) {
        viewModelScope.launch { groupRepository.removeMember(groupId, uid) }
    }

    fun leaveGroup() {
        viewModelScope.launch { groupRepository.leaveGroup(groupId) }
    }
}
