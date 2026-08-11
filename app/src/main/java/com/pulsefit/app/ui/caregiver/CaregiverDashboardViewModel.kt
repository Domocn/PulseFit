package com.pulsefit.app.ui.caregiver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.remote.CaregiverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaregiverDashboardViewModel @Inject constructor(
    private val repository: CaregiverRepository
) : ViewModel() {

    val linkedAthletes: StateFlow<List<CaregiverRepository.CaregiverLink>> = repository.getLinkedAthletes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedReadiness = MutableStateFlow<CaregiverRepository.SharedReadiness?>(null)
    val selectedReadiness: StateFlow<CaregiverRepository.SharedReadiness?> = _selectedReadiness

    fun loadReadiness(athleteUid: String) {
        viewModelScope.launch {
            _selectedReadiness.value = repository.getAthleteReadiness(athleteUid)
        }
    }
}
