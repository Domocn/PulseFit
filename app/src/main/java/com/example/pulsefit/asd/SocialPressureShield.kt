package com.example.pulsefit.asd

import com.example.pulsefit.data.repository.SensoryPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialPressureShield @Inject constructor(
    private val sensoryPreferencesRepository: SensoryPreferencesRepository
) {
    val isEnabled: Flow<Boolean> = sensoryPreferencesRepository.getPreferences()
        .map { it?.socialPressureShield ?: false }

    suspend fun setEnabled(enabled: Boolean) {
        val prefs = sensoryPreferencesRepository.getPreferencesOnce()
        sensoryPreferencesRepository.save(prefs.copy(socialPressureShield = enabled))
    }
}
