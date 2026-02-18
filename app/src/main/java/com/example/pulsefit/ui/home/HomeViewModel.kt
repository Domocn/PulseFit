package com.example.pulsefit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.ble.ConnectionStatus
import com.example.pulsefit.ble.HeartRateSource
import com.example.pulsefit.domain.model.UserProfile
import com.example.pulsefit.domain.repository.WorkoutRepository
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import com.example.pulsefit.domain.usecase.StartWorkoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase,
    private val startWorkout: StartWorkoutUseCase,
    private val workoutRepository: WorkoutRepository,
    private val heartRateSource: HeartRateSource
) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayBurnPoints: StateFlow<Int> = workoutRepository.getTodayBurnPoints()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val connectionStatus: StateFlow<ConnectionStatus> = heartRateSource.connectionStatus

    private val _workoutId = MutableStateFlow<Long?>(null)
    val workoutId: StateFlow<Long?> = _workoutId

    fun onStartWorkout() {
        viewModelScope.launch {
            val id = startWorkout()
            _workoutId.value = id
        }
    }

    fun onWorkoutNavigated() {
        _workoutId.value = null
    }
}
