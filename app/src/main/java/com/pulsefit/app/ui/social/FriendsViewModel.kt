package com.pulsefit.app.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.remote.FriendRequest
import com.pulsefit.app.data.remote.FriendsRepository
import com.pulsefit.app.data.remote.PublicProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository
) : ViewModel() {

    private val _friends = MutableStateFlow<List<PublicProfile>>(emptyList())
    val friends: StateFlow<List<PublicProfile>> = _friends

    private val _pendingRequests = MutableStateFlow<List<FriendRequest>>(emptyList())
    val pendingRequests: StateFlow<List<FriendRequest>> = _pendingRequests

    init {
        viewModelScope.launch {
            friendsRepository.getFriends().collect { _friends.value = it }
        }
        viewModelScope.launch {
            friendsRepository.getPendingRequests().collect { _pendingRequests.value = it }
        }
    }

    fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            friendsRepository.acceptFriendRequest(requestId)
        }
    }

    fun declineRequest(requestId: String) {
        viewModelScope.launch {
            friendsRepository.declineFriendRequest(requestId)
        }
    }

    fun removeFriend(uid: String) {
        viewModelScope.launch {
            friendsRepository.removeFriend(uid)
        }
    }
}
