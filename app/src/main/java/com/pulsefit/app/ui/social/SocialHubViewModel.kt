package com.pulsefit.app.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.asd.SocialPressureShield
import com.pulsefit.app.data.remote.FriendsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialHubViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val socialPressureShield: SocialPressureShield
) : ViewModel() {

    private val _isShielded = MutableStateFlow(false)
    val isShielded: StateFlow<Boolean> = _isShielded

    private val _friendCount = MutableStateFlow(0)
    val friendCount: StateFlow<Int> = _friendCount

    private val _pendingCount = MutableStateFlow(0)
    val pendingCount: StateFlow<Int> = _pendingCount

    init {
        viewModelScope.launch {
            _isShielded.value = socialPressureShield.isEnabled.first()
        }
        viewModelScope.launch {
            friendsRepository.getFriends().collect { _friendCount.value = it.size }
        }
        viewModelScope.launch {
            friendsRepository.getPendingRequests().collect { _pendingCount.value = it.size }
        }
    }
}
