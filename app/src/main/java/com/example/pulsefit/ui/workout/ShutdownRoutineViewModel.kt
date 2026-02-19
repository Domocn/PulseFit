package com.example.pulsefit.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.model.NdProfile
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShutdownRoutineViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase
) : ViewModel() {

    private val _ndProfile = MutableStateFlow<NdProfile?>(null)
    val ndProfile: StateFlow<NdProfile?> = _ndProfile

    init {
        viewModelScope.launch {
            val profile = getUserProfile.once()
            _ndProfile.value = profile?.ndProfile ?: NdProfile.STANDARD
        }
    }
}
