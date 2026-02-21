package com.pulsefit.app.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.remote.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinGroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _isJoining = MutableStateFlow(false)
    val isJoining: StateFlow<Boolean> = _isJoining

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun joinGroup(code: String, onSuccess: () -> Unit) {
        val trimmed = code.trim().uppercase()
        if (trimmed.length != 6) {
            _error.value = "Invite code must be 6 characters"
            return
        }
        _isJoining.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val groupId = groupRepository.joinGroupByCode(trimmed)
                if (groupId != null) {
                    onSuccess()
                } else {
                    _error.value = "Invalid code or group is full"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to join group"
            } finally {
                _isJoining.value = false
            }
        }
    }
}
