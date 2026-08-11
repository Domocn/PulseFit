package com.pulsefit.app.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.remote.BuddyMatchingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuddyMatchViewModel @Inject constructor(
    private val repository: BuddyMatchingRepository
) : ViewModel() {

    val matches: StateFlow<List<BuddyMatchingRepository.BuddyMatch>> = repository.getMatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _hasActiveRequest = MutableStateFlow(false)
    val hasActiveRequest: StateFlow<Boolean> = _hasActiveRequest

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    init {
        viewModelScope.launch {
            _hasActiveRequest.value = repository.hasActiveRequest()
        }
    }

    fun submitRequest(ndProfile: String, preferredTimes: List<String>, parallelPlayOnly: Boolean) {
        viewModelScope.launch {
            _isSubmitting.value = true
            repository.submitRequest(ndProfile, preferredTimes, parallelPlayOnly)
            _hasActiveRequest.value = true
            _isSubmitting.value = false
        }
    }

    fun cancelRequest() {
        viewModelScope.launch {
            repository.cancelRequest()
            _hasActiveRequest.value = false
        }
    }
}
