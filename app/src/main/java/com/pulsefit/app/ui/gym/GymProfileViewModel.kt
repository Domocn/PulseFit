package com.pulsefit.app.ui.gym

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.local.entity.GymProfileEntity
import com.pulsefit.app.data.repository.GymProfileRepository
import com.pulsefit.app.util.GymBusyPredictor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GymProfileViewModel @Inject constructor(
    private val repository: GymProfileRepository,
    private val busyPredictor: GymBusyPredictor
) : ViewModel() {

    val gyms: StateFlow<List<GymProfileEntity>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editingGym = MutableStateFlow<GymProfileEntity?>(null)
    val editingGym: StateFlow<GymProfileEntity?> = _editingGym

    private val _busyPrediction = MutableStateFlow<GymBusyPredictor.BusyPrediction?>(null)
    val busyPrediction: StateFlow<GymBusyPredictor.BusyPrediction?> = _busyPrediction

    fun startEditing(gym: GymProfileEntity? = null) {
        _editingGym.value = gym ?: GymProfileEntity(name = "")
    }

    fun cancelEditing() { _editingGym.value = null }

    fun saveGym(gym: GymProfileEntity) {
        viewModelScope.launch {
            if (gym.id == 0L) repository.insert(gym) else repository.update(gym)
            _editingGym.value = null
        }
    }

    fun deleteGym(gym: GymProfileEntity) {
        viewModelScope.launch { repository.delete(gym) }
    }

    fun toggleFavorite(gym: GymProfileEntity) {
        viewModelScope.launch { repository.update(gym.copy(isFavorite = !gym.isFavorite)) }
    }

    fun predictBusyness(crowdRating: Int = 3) {
        _busyPrediction.value = busyPredictor.predict(crowdRating)
    }
}
