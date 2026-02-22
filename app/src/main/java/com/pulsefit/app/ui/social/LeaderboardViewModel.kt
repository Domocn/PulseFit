package com.pulsefit.app.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.asd.SocialPressureShield
import com.pulsefit.app.data.remote.CloudProfileRepository
import com.pulsefit.app.data.remote.FriendsRepository
import com.pulsefit.app.data.remote.PublicProfile
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val cloudProfileRepository: CloudProfileRepository,
    private val socialPressureShield: SocialPressureShield,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _entries = MutableStateFlow<List<PublicProfile>>(emptyList())
    val entries: StateFlow<List<PublicProfile>> = _entries

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
            loadLeaderboard()
        }
    }

    private suspend fun loadLeaderboard() {
        _isLoading.value = true
        val friendUids = friendsRepository.getFriendUids()
        val profiles = mutableListOf<PublicProfile>()

        // Add current user
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val myProfile = cloudProfileRepository.getPublicProfile(currentUser.uid)
            if (myProfile != null) {
                profiles.add(myProfile)
            }
        }

        // Add friends
        for (uid in friendUids) {
            val profile = cloudProfileRepository.getPublicProfile(uid)
            if (profile != null) profiles.add(profile)
        }

        _entries.value = profiles.sortedByDescending { it.weeklyBurnPoints }
        _isLoading.value = false
    }

    fun currentUserUid(): String? = firebaseAuth.currentUser?.uid
}
