package com.pulsefit.app.adhd

import com.pulsefit.app.util.HapticManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class FidgetHaptics @Inject constructor(
    private val hapticManager: HapticManager
) {
    private var fidgetJob: Job? = null
    private var currentPattern = 0

    suspend fun startDuringRest() = coroutineScope {
        fidgetJob?.cancel()
        fidgetJob = launch {
            while (isActive) {
                hapticManager.fidgetPattern(currentPattern)
                delay(1000)
            }
        }
    }

    fun stop() {
        fidgetJob?.cancel()
        fidgetJob = null
    }

    fun cyclePattern() {
        currentPattern = (currentPattern + 1) % 4
    }
}
