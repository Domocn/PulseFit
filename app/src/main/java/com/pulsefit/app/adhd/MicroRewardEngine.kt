package com.pulsefit.app.adhd

import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.data.model.HapticLevel
import com.pulsefit.app.data.repository.SensoryPreferencesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

data class MicroRewardEvent(
    val points: Int,
    val zone: HeartRateZone,
    val message: String
)

class MicroRewardEngine @Inject constructor(
    private val sensoryPreferencesRepository: SensoryPreferencesRepository
) {
    private val _rewardEvents = MutableSharedFlow<MicroRewardEvent>(extraBufferCapacity = 10)
    val rewardEvents: SharedFlow<MicroRewardEvent> = _rewardEvents

    private var lastRewardSecond = 0
    private val rewardIntervalSeconds = 30

    suspend fun onTick(elapsedSeconds: Int, zone: HeartRateZone) {
        if (zone.pointsPerMinute <= 0) return
        if (elapsedSeconds - lastRewardSecond >= rewardIntervalSeconds) {
            lastRewardSecond = elapsedSeconds
            val pointsForInterval = (zone.pointsPerMinute * rewardIntervalSeconds) / 60
            if (pointsForInterval > 0) {
                _rewardEvents.emit(
                    MicroRewardEvent(
                        points = pointsForInterval,
                        zone = zone,
                        message = "+$pointsForInterval"
                    )
                )
            }
        }
    }

    suspend fun getHapticLevel(): HapticLevel {
        return sensoryPreferencesRepository.getPreferencesOnce().hapticLevel
    }

    fun reset() {
        lastRewardSecond = 0
    }
}
