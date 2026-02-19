package com.example.pulsefit.asd

import com.example.pulsefit.data.model.HeartRateZone
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

data class TransitionWarning(
    val message: String,
    val secondsUntil: Int,
    val fromZone: HeartRateZone,
    val toZone: HeartRateZone?
)

class TransitionWarningManager @Inject constructor() {
    private val _warnings = MutableSharedFlow<TransitionWarning>(extraBufferCapacity = 5)
    val warnings: SharedFlow<TransitionWarning> = _warnings

    private var lastZone: HeartRateZone? = null
    private var zoneStableSeconds = 0

    suspend fun onTick(currentZone: HeartRateZone, elapsedSeconds: Int) {
        if (currentZone != lastZone) {
            if (lastZone != null) {
                _warnings.emit(
                    TransitionWarning(
                        message = "Zone changed to ${currentZone.label}",
                        secondsUntil = 0,
                        fromZone = lastZone!!,
                        toZone = currentZone
                    )
                )
            }
            lastZone = currentZone
            zoneStableSeconds = 0
        } else {
            zoneStableSeconds++
        }
    }

    fun reset() {
        lastZone = null
        zoneStableSeconds = 0
    }
}
