package com.pulsefit.app.ui.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.exercise.ChallengeRegistry
import com.pulsefit.app.data.model.ChallengeDefinition
import com.pulsefit.app.data.model.ChallengeDuration
import com.pulsefit.app.data.remote.UserChallengeRepository
import com.pulsefit.app.data.remote.model.UserChallenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val challengeRegistry: ChallengeRegistry,
    private val userChallengeRepository: UserChallengeRepository
) : ViewModel() {

    private val _availableChallenges = MutableStateFlow<List<ChallengeDefinition>>(emptyList())
    val availableChallenges: StateFlow<List<ChallengeDefinition>> = _availableChallenges

    private val _activeChallenges = MutableStateFlow<List<UserChallenge>>(emptyList())
    val activeChallenges: StateFlow<List<UserChallenge>> = _activeChallenges

    private val _pastChallenges = MutableStateFlow<List<UserChallenge>>(emptyList())
    val pastChallenges: StateFlow<List<UserChallenge>> = _pastChallenges

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        _availableChallenges.value = challengeRegistry.getAll()

        viewModelScope.launch {
            userChallengeRepository.getActiveChallenges()
                .catch { emit(emptyList()) }
                .collect { _activeChallenges.value = it }
        }

        viewModelScope.launch {
            userChallengeRepository.getAllChallenges()
                .catch { emit(emptyList()) }
                .collect { all ->
                    _pastChallenges.value = all.filter {
                        it.status != com.pulsefit.app.data.remote.model.ChallengeStatus.ACTIVE
                    }
                    _isLoading.value = false
                }
        }
    }

    fun startChallenge(definition: ChallengeDefinition) {
        viewModelScope.launch {
            userChallengeRepository.startChallenge(definition)
        }
    }

    fun abandonChallenge(challengeId: String) {
        viewModelScope.launch {
            userChallengeRepository.abandonChallenge(challengeId)
        }
    }

    fun getSingleSessionChallenges(): List<ChallengeDefinition> =
        challengeRegistry.getSingleSession()

    fun getMultiDayChallenges(): List<ChallengeDefinition> =
        challengeRegistry.getMultiDay()
}
