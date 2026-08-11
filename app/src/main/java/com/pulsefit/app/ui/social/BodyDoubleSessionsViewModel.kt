package com.pulsefit.app.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.local.entity.ScheduledBodyDoubleEntity
import com.pulsefit.app.data.repository.ScheduledBodyDoubleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BodyDoubleSessionsViewModel @Inject constructor(
    private val repository: ScheduledBodyDoubleRepository
) : ViewModel() {

    val upcomingSessions: StateFlow<List<ScheduledBodyDoubleEntity>> = repository.getUpcoming()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showCreate = MutableStateFlow(false)
    val showCreate: StateFlow<Boolean> = _showCreate

    fun toggleCreate() { _showCreate.value = !_showCreate.value }

    fun createSession(title: String, scheduledAt: Long, durationMinutes: Int) {
        viewModelScope.launch {
            repository.createSession(title, scheduledAt, durationMinutes)
            _showCreate.value = false
        }
    }

    fun deleteSession(id: Long) {
        viewModelScope.launch { repository.deleteLocal(id) }
    }
}
