package com.pulsefit.app.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.domain.model.MuscleFatigue
import com.pulsefit.app.domain.repository.MuscleFatigueRepository
import com.pulsefit.app.util.MuscleFatigueCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MuscleFatigueState(
    val allFatigue: List<MuscleFatigue> = emptyList(),
    val fatigueMap: Map<String, MuscleFatigue> = emptyMap(),
    val selectedMuscle: String? = null,
    val selectedFatigue: MuscleFatigue? = null,
    val readyMuscles: List<MuscleFatigue> = emptyList(),
    val fatiguedMuscles: List<MuscleFatigue> = emptyList(),
    val isRecalculating: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MuscleFatigueViewModel @Inject constructor(
    private val repository: MuscleFatigueRepository,
    private val calculator: MuscleFatigueCalculator
) : ViewModel() {

    private val _state = MutableStateFlow(MuscleFatigueState())
    val state: StateFlow<MuscleFatigueState> = _state

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val all = repository.getAll()
            val ready = repository.getReadyMuscles()
            val fatigued = repository.getFatiguedMuscles()
            _state.value = _state.value.copy(
                allFatigue = all,
                fatigueMap = all.associateBy { it.muscleGroup },
                readyMuscles = ready,
                fatiguedMuscles = fatigued
            )
        }
    }

    fun selectMuscle(muscleGroup: String) {
        val fatigue = _state.value.fatigueMap[muscleGroup]
        _state.value = _state.value.copy(
            selectedMuscle = if (_state.value.selectedMuscle == muscleGroup) null else muscleGroup,
            selectedFatigue = fatigue
        )
    }

    fun recalculate() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isRecalculating = true, error = null)
            try {
                repository.recalculateAll()
                loadData()
                _state.value = _state.value.copy(isRecalculating = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isRecalculating = false,
                    error = e.message ?: "Failed to recalculate fatigue"
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
