package com.pulsefit.app.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.asd.SocialPressureShield
import com.pulsefit.app.data.remote.CloudProfileRepository
import com.pulsefit.app.data.remote.FriendsRepository
import com.pulsefit.app.data.remote.SharedWorkout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val cloudProfileRepository: CloudProfileRepository,
    private val socialPressureShield: SocialPressureShield
) : ViewModel() {

    private val _workouts = MutableStateFlow<List<SharedWorkout>>(emptyList())
    val workouts: StateFlow<List<SharedWorkout>> = _workouts

    private val _isShielded = MutableStateFlow(false)
    val isShielded: StateFlow<Boolean> = _isShielded

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            _isShielded.value = socialPressureShield.isEnabled.first()
            if (_isShielded.value) {
                _isLoading.value = false
                return@launch
            }
            loadFeed()
        }
    }

    private suspend fun loadFeed() {
        _isLoading.value = true
        val friendUids = friendsRepository.getFriendUids()
        _workouts.value = cloudProfileRepository.getSharedWorkouts(friendUids)
        _isLoading.value = false
    }
}
