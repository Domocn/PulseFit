package com.example.pulsefit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.ble.ConnectionStatus
import com.example.pulsefit.ble.HeartRateSource
import com.example.pulsefit.domain.model.UserProfile
import com.example.pulsefit.domain.model.Workout
import com.example.pulsefit.domain.repository.WorkoutRepository
import com.example.pulsefit.domain.usecase.CalculateStreakUseCase
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import com.example.pulsefit.domain.usecase.GetWorkoutStatsUseCase
import com.example.pulsefit.domain.usecase.StartWorkoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase,
    private val startWorkout: StartWorkoutUseCase,
    private val workoutRepository: WorkoutRepository,
    private val heartRateSource: HeartRateSource,
    private val calculateStreak: CalculateStreakUseCase,
    private val getWorkoutStats: GetWorkoutStatsUseCase
) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayBurnPoints: StateFlow<Int> = workoutRepository.getTodayBurnPoints()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val connectionStatus: StateFlow<ConnectionStatus> = heartRateSource.connectionStatus

    private val _workoutId = MutableStateFlow<Long?>(null)
    val workoutId: StateFlow<Long?> = _workoutId

    private val _isJustFiveMin = MutableStateFlow(false)
    val isJustFiveMin: StateFlow<Boolean> = _isJustFiveMin

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak

    private val _weeklyWorkouts = MutableStateFlow(0)
    val weeklyWorkouts: StateFlow<Int> = _weeklyWorkouts

    private val _weeklyBurnPoints = MutableStateFlow(0)
    val weeklyBurnPoints: StateFlow<Int> = _weeklyBurnPoints

    init {
        viewModelScope.launch {
            _currentStreak.value = calculateStreak()
        }
        viewModelScope.launch {
            val stats = getWorkoutStats.getWeeklyStats()
            _weeklyWorkouts.value = stats.totalWorkouts
            _weeklyBurnPoints.value = stats.totalBurnPoints
        }
    }

    fun onStartWorkout() {
        _isJustFiveMin.value = false
        viewModelScope.launch {
            val workout = Workout(startTime = Instant.now())
            val id = workoutRepository.createWorkout(workout)
            _workoutId.value = id
        }
    }

    fun onStartJustFiveMin() {
        _isJustFiveMin.value = true
        viewModelScope.launch {
            val workout = Workout(startTime = Instant.now(), isJustFiveMin = true)
            val id = workoutRepository.createWorkout(workout)
            _workoutId.value = id
        }
    }

    fun onWorkoutNavigated() {
        _workoutId.value = null
    }
}
