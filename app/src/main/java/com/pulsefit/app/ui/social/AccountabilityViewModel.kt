package com.pulsefit.app.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.remote.AccountabilityContractRepository
import com.pulsefit.app.data.remote.FriendsRepository
import com.pulsefit.app.data.remote.PublicProfile
import com.pulsefit.app.data.remote.model.AccountabilityContract
import com.pulsefit.app.data.remote.model.WeeklyProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountabilityViewModel @Inject constructor(
    private val contractRepository: AccountabilityContractRepository,
    private val friendsRepository: FriendsRepository
) : ViewModel() {

    private val _contracts = MutableStateFlow<List<AccountabilityContract>>(emptyList())
    val contracts: StateFlow<List<AccountabilityContract>> = _contracts

    private val _selectedProgress = MutableStateFlow<WeeklyProgress?>(null)
    val selectedProgress: StateFlow<WeeklyProgress?> = _selectedProgress

    private val _friends = MutableStateFlow<List<PublicProfile>>(emptyList())
    val friends: StateFlow<List<PublicProfile>> = _friends

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog

    init {
        viewModelScope.launch {
            contractRepository.getActiveContracts().collect { list ->
                _contracts.value = list
            }
        }
        viewModelScope.launch {
            friendsRepository.getFriends().collect { list ->
                _friends.value = list
            }
        }
    }

    fun selectContract(contractId: String) {
        viewModelScope.launch {
            contractRepository.getWeeklyProgress(contractId).collect { progress ->
                _selectedProgress.value = progress
            }
        }
    }

    fun createContract(friendUid: String, friendName: String, weeklyGoal: Int) {
        viewModelScope.launch {
            contractRepository.createContract(friendUid, friendName, weeklyGoal)
            _showCreateDialog.value = false
        }
    }

    fun cancelContract(contractId: String) {
        viewModelScope.launch {
            contractRepository.cancelContract(contractId)
        }
    }

    fun showCreate() { _showCreateDialog.value = true }
    fun hideCreate() { _showCreateDialog.value = false }
}
