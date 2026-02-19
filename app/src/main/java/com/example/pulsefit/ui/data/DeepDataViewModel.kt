package com.example.pulsefit.ui.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.domain.model.Workout
import com.example.pulsefit.domain.model.WorkoutStats
import com.example.pulsefit.domain.repository.WorkoutRepository
import com.example.pulsefit.domain.usecase.GetWorkoutStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeepDataViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val getWorkoutStats: GetWorkoutStatsUseCase
) : ViewModel() {

    val workouts: StateFlow<List<Workout>> = workoutRepository.getAllWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _stats = MutableStateFlow<WorkoutStats?>(null)
    val stats: StateFlow<WorkoutStats?> = _stats

    init {
        viewModelScope.launch {
            val completedWorkouts = workoutRepository.getCompletedWorkouts()
            if (completedWorkouts.isNotEmpty()) {
                _stats.value = WorkoutStats(
                    totalWorkouts = completedWorkouts.size,
                    totalBurnPoints = completedWorkouts.sumOf { it.burnPoints },
                    totalDurationSeconds = completedWorkouts.sumOf { it.durationSeconds },
                    averageBurnPoints = completedWorkouts.sumOf { it.burnPoints } / completedWorkouts.size,
                    averageDurationSeconds = completedWorkouts.sumOf { it.durationSeconds } / completedWorkouts.size
                )
            }
        }
    }
}
