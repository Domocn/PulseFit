package com.pulsefit.app.ui.caregiver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.remote.CaregiverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaregiverSetupViewModel @Inject constructor(
    private val repository: CaregiverRepository
) : ViewModel() {

    private val _linkCode = MutableStateFlow<String?>(null)
    val linkCode: StateFlow<String?> = _linkCode

    private val _claimResult = MutableStateFlow<String?>(null)
    val claimResult: StateFlow<String?> = _claimResult

    fun generateCode() {
        viewModelScope.launch {
            _linkCode.value = repository.generateLinkCode()
        }
    }

    fun claimCode(code: String) {
        viewModelScope.launch {
            val success = repository.claimLinkCode(code)
            _claimResult.value = if (success) "Linked successfully" else "Invalid or already claimed code"
        }
    }

    fun dismissResult() { _claimResult.value = null }
}
