package com.example.pulsefit.asd

import com.example.pulsefit.data.repository.SensoryPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PredictableUiManager @Inject constructor(
    private val sensoryPreferencesRepository: SensoryPreferencesRepository
) {
    val isUiLocked: Flow<Boolean> = sensoryPreferencesRepository.getPreferences()
        .map { it?.uiLocked ?: false }

    suspend fun setUiLocked(locked: Boolean) {
        val prefs = sensoryPreferencesRepository.getPreferencesOnce()
        sensoryPreferencesRepository.save(prefs.copy(uiLocked = locked))
    }
}
