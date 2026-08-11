package com.pulsefit.app.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.domain.model.BodyMeasurement
import com.pulsefit.app.domain.repository.BodyMeasurementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class BodyMeasurementsState(
    val measurements: List<BodyMeasurement> = emptyList(),
    val latest: BodyMeasurement? = null,
    val weightHistory: List<BodyMeasurement> = emptyList(),
    val bodyFatHistory: List<BodyMeasurement> = emptyList(),
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BodyMeasurementsViewModel @Inject constructor(
    private val repository: BodyMeasurementRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BodyMeasurementsState())
    val state: StateFlow<BodyMeasurementsState> = _state

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val all = repository.getAll()
            val latest = repository.getLatest()
            val weightHistory = repository.getWeightHistory()
            val bodyFatHistory = repository.getBodyFatHistory()
            _state.value = _state.value.copy(
                measurements = all.sortedByDescending { it.timestamp },
                latest = latest,
                weightHistory = weightHistory.sortedBy { it.timestamp },
                bodyFatHistory = bodyFatHistory.sortedBy { it.timestamp }
            )
        }
    }

    fun saveMeasurement(
        weightKg: Float?,
        bodyFatPercent: Float?,
        chestCm: Float?,
        waistCm: Float?,
        hipsCm: Float?,
        leftArmCm: Float?,
        rightArmCm: Float?,
        leftThighCm: Float?,
        rightThighCm: Float?,
        leftCalfCm: Float?,
        rightCalfCm: Float?,
        neckCm: Float?,
        photoUri: String?,
        notes: String?
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)
            try {
                val measurement = BodyMeasurement(
                    timestamp = Instant.now(),
                    weightKg = weightKg,
                    bodyFatPercent = bodyFatPercent,
                    chestCm = chestCm,
                    waistCm = waistCm,
                    hipsCm = hipsCm,
                    leftArmCm = leftArmCm,
                    rightArmCm = rightArmCm,
                    leftThighCm = leftThighCm,
                    rightThighCm = rightThighCm,
                    leftCalfCm = leftCalfCm,
                    rightCalfCm = rightCalfCm,
                    neckCm = neckCm,
                    photoUri = photoUri,
                    notes = notes
                )
                repository.insert(measurement)
                _state.value = _state.value.copy(isSaving = false)
                loadData()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save measurement"
                )
            }
        }
    }

    fun deleteMeasurement(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteById(id)
                loadData()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to delete measurement"
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
