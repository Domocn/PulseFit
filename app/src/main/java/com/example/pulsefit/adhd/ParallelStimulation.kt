package com.example.pulsefit.adhd

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParallelStimulation @Inject constructor() {
    // Tracks whether the workout is running in background mode
    // (user has navigated away to another app)
    var isBackgroundMode = false
        private set

    fun enterBackgroundMode() {
        isBackgroundMode = true
    }

    fun exitBackgroundMode() {
        isBackgroundMode = false
    }
}
