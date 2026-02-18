package com.example.pulsefit.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.domain.model.HeartRateReading
import com.example.pulsefit.domain.model.Workout
import com.example.pulsefit.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _workout = MutableStateFlow<Workout?>(null)
    val workout: StateFlow<Workout?> = _workout

    private val _readings = MutableStateFlow<List<HeartRateReading>>(emptyList())
    val readings: StateFlow<List<HeartRateReading>> = _readings

    fun load(workoutId: Long) {
        viewModelScope.launch {
            _workout.value = workoutRepository.getWorkoutById(workoutId)
            _readings.value = workoutRepository.getReadingsForWorkoutOnce(workoutId)
        }
    }
}
