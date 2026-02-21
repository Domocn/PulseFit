package com.pulsefit.app.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.remote.GroupRepository
import com.pulsefit.app.data.remote.model.GroupType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateGroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _isCreating = MutableStateFlow(false)
    val isCreating: StateFlow<Boolean> = _isCreating

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun createGroup(name: String, type: GroupType, description: String, onSuccess: () -> Unit) {
        if (name.isBlank()) {
            _error.value = "Group name is required"
            return
        }
        _isCreating.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                groupRepository.createGroup(name, type, description)
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to create group"
            } finally {
                _isCreating.value = false
            }
        }
    }
}
