package com.pulsefit.app.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.exercise.StrengthExerciseRegistry
import com.pulsefit.app.data.model.StrengthExercise
import com.pulsefit.app.domain.model.ExerciseLog
import com.pulsefit.app.domain.model.PersonalRecord
import com.pulsefit.app.domain.repository.ExerciseLogRepository
import com.pulsefit.app.domain.repository.PersonalRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseDetailState(
    val exercise: StrengthExercise? = null,
    val exerciseLogs: List<ExerciseLog> = emptyList(),
    val personalRecords: List<PersonalRecord> = emptyList(),
    val currentBest: PersonalRecord? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    private val exerciseLogRepository: ExerciseLogRepository,
    private val personalRecordRepository: PersonalRecordRepository,
    private val exerciseRegistry: StrengthExerciseRegistry
) : ViewModel() {

    private val _state = MutableStateFlow(ExerciseDetailState())
    val state: StateFlow<ExerciseDetailState> = _state

    fun loadExercise(exerciseId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val exercise = exerciseRegistry.getById(exerciseId)
                val logs = exerciseLogRepository.getByExerciseId(exerciseId)
                val records = personalRecordRepository.getByExerciseId(exerciseId)
                val best = personalRecordRepository.getCurrentBest(exerciseId)

                _state.value = ExerciseDetailState(
                    exercise = exercise,
                    exerciseLogs = logs,
                    personalRecords = records,
                    currentBest = best,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load exercise"
                )
            }
        }
    }
}
