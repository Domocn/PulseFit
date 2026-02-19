package com.example.pulsefit.asd

import javax.inject.Inject
import javax.inject.Singleton

/**
 * F144: Fixed sound mapping for ASD profiles.
 * Every event always produces the same sound, no variation.
 */
@Singleton
class AudioPalette @Inject constructor() {
    enum class SoundEvent {
        ZONE_UP,       // rising tone
        ZONE_DOWN,     // falling tone
        POINT_EARNED,  // soft click
        MILESTONE,     // chime
        WORKOUT_START, // bell
        WORKOUT_END    // completion tone
    }

    // Returns the tone frequency in Hz for each event
    fun toneForEvent(event: SoundEvent): Int {
        return when (event) {
            SoundEvent.ZONE_UP -> 880
            SoundEvent.ZONE_DOWN -> 440
            SoundEvent.POINT_EARNED -> 660
            SoundEvent.MILESTONE -> 1046
            SoundEvent.WORKOUT_START -> 523
            SoundEvent.WORKOUT_END -> 784
        }
    }

    fun durationForEvent(event: SoundEvent): Int {
        return when (event) {
            SoundEvent.ZONE_UP -> 200
            SoundEvent.ZONE_DOWN -> 200
            SoundEvent.POINT_EARNED -> 100
            SoundEvent.MILESTONE -> 500
            SoundEvent.WORKOUT_START -> 300
            SoundEvent.WORKOUT_END -> 600
        }
    }
}
