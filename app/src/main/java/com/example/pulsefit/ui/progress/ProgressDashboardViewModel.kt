package com.example.pulsefit.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.domain.model.UserProfile
import com.example.pulsefit.domain.model.WorkoutStats
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import com.example.pulsefit.domain.usecase.GetWorkoutStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressDashboardViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase,
    private val getWorkoutStats: GetWorkoutStatsUseCase
) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _weeklyStats = MutableStateFlow<WorkoutStats?>(null)
    val weeklyStats: StateFlow<WorkoutStats?> = _weeklyStats

    private val _monthlyStats = MutableStateFlow<WorkoutStats?>(null)
    val monthlyStats: StateFlow<WorkoutStats?> = _monthlyStats

    init {
        viewModelScope.launch {
            _weeklyStats.value = getWorkoutStats.getWeeklyStats()
            _monthlyStats.value = getWorkoutStats.getMonthlyStats()
        }
    }
}
