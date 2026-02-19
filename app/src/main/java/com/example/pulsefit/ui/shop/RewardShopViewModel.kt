package com.example.pulsefit.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardShopViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase
) : ViewModel() {

    private val _totalXp = MutableStateFlow(0L)
    val totalXp: StateFlow<Long> = _totalXp

    init {
        viewModelScope.launch {
            getUserProfile().collect { profile ->
                _totalXp.value = profile?.totalXp ?: 0
            }
        }
    }
}
