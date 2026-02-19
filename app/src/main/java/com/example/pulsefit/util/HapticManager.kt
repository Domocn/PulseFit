package com.example.pulsefit.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.pulsefit.data.model.HapticLevel
import com.example.pulsefit.data.repository.SensoryPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HapticManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sensoryPreferencesRepository: SensoryPreferencesRepository
) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var currentLevel = HapticLevel.STRONG

    suspend fun updateLevel() {
        currentLevel = sensoryPreferencesRepository.getPreferencesOnce().hapticLevel
    }

    fun zoneChange() {
        if (currentLevel == HapticLevel.OFF) return
        val duration = if (currentLevel == HapticLevel.GENTLE) 50L else 100L
        vibrate(duration)
    }

    fun pointEarned() {
        if (currentLevel == HapticLevel.OFF) return
        val duration = if (currentLevel == HapticLevel.GENTLE) 30L else 60L
        vibrate(duration)
    }

    fun milestone() {
        if (currentLevel == HapticLevel.OFF) return
        val pattern = if (currentLevel == HapticLevel.GENTLE) {
            longArrayOf(0, 50, 50, 50)
        } else {
            longArrayOf(0, 100, 50, 100, 50, 100)
        }
        vibratePattern(pattern)
    }

    fun chunkComplete() {
        if (currentLevel == HapticLevel.OFF) return
        val duration = if (currentLevel == HapticLevel.GENTLE) 40L else 80L
        vibrate(duration)
    }

    fun fidgetPattern(patternType: Int) {
        if (currentLevel == HapticLevel.OFF) return
        val pattern = when (patternType) {
            0 -> longArrayOf(0, 60, 940) // heartbeat
            1 -> longArrayOf(0, 50, 450, 50, 450) // metronome
            2 -> longArrayOf(0, 20, 80, 40, 60, 60, 40, 80, 20) // wave
            else -> longArrayOf(0, 30, (100..500).random().toLong()) // random
        }
        vibratePattern(pattern)
    }

    private fun vibrate(durationMs: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(durationMs)
        }
    }

    private fun vibratePattern(pattern: LongArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, -1)
        }
    }
}
