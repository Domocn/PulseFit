package com.pulsefit.app.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.remote.FriendsRepository
import com.pulsefit.app.data.remote.PublicProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFriendViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _results = MutableStateFlow<List<PublicProfile>>(emptyList())
    val results: StateFlow<List<PublicProfile>> = _results

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _sentRequests = MutableStateFlow<Set<String>>(emptySet())
    val sentRequests: StateFlow<Set<String>> = _sentRequests

    private var searchJob: Job? = null

    fun updateQuery(value: String) {
        _query.value = value
        searchJob?.cancel()
        if (value.length < 2) {
            _results.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            delay(300) // debounce
            _isSearching.value = true
            _results.value = friendsRepository.searchUsers(value)
            _isSearching.value = false
        }
    }

    fun sendRequest(uid: String) {
        viewModelScope.launch {
            friendsRepository.sendFriendRequest(uid)
            _sentRequests.value = _sentRequests.value + uid
        }
    }
}
