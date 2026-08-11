package com.pulsefit.app.ui.ritual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.local.entity.RitualStepEntity
import com.pulsefit.app.data.model.RitualType
import com.pulsefit.app.data.repository.RitualRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RitualViewModel @Inject constructor(
    private val repository: RitualRepository
) : ViewModel() {

    private val _selectedType = MutableStateFlow(RitualType.PRE)
    val selectedType: StateFlow<RitualType> = _selectedType

    val steps: StateFlow<List<RitualStepEntity>> = _selectedType.flatMapLatest { type ->
        repository.getSteps(type)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.ensureDefaults(RitualType.PRE)
            repository.ensureDefaults(RitualType.POST)
        }
    }

    fun selectType(type: RitualType) { _selectedType.value = type }

    fun toggleStep(step: RitualStepEntity) {
        viewModelScope.launch { repository.toggleStep(step) }
    }

    fun addStep(label: String) {
        viewModelScope.launch { repository.addStep(_selectedType.value, label) }
    }

    fun deleteStep(id: Long) {
        viewModelScope.launch { repository.deleteStep(id) }
    }

    fun resetAll() {
        viewModelScope.launch { repository.resetAll(_selectedType.value) }
    }

    fun resetToDefaults() {
        viewModelScope.launch { repository.clearAndSeedDefaults(_selectedType.value) }
    }
}
