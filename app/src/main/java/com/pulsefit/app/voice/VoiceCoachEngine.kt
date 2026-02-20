package com.pulsefit.app.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.data.model.VoiceCoachStyle
import com.pulsefit.app.data.repository.SensoryPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceCoachEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sensoryPreferencesRepository: SensoryPreferencesRepository
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    private var currentStyle = VoiceCoachStyle.STANDARD
    private var lastZone: HeartRateZone? = null
    private var lastSpokenMinute = -1

    fun initialize() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
                isInitialized = true
            }
        }
    }

    suspend fun updateStyle() {
        currentStyle = sensoryPreferencesRepository.getPreferencesOnce().voiceCoachStyle
    }

    fun onZoneChange(newZone: HeartRateZone) {
        if (!isInitialized || newZone == lastZone) return
        lastZone = newZone

        val message = when (currentStyle) {
            VoiceCoachStyle.LITERAL -> "Zone changed to ${newZone.label}. Points per minute: ${newZone.pointsPerMinute}."
            VoiceCoachStyle.STANDARD -> when (newZone) {
                HeartRateZone.REST -> "Take it easy."
                HeartRateZone.WARM_UP -> "Warming up nicely."
                HeartRateZone.ACTIVE -> "Good pace. Earning points."
                HeartRateZone.PUSH -> "Pushing hard. Great effort!"
                HeartRateZone.PEAK -> "You're on fire! Maximum points!"
            }
            VoiceCoachStyle.HYPE -> when (newZone) {
                HeartRateZone.REST -> "Chill mode. You got this!"
                HeartRateZone.WARM_UP -> "Let's warm it up! Getting started!"
                HeartRateZone.ACTIVE -> "YES! Active zone! Points are rolling in!"
                HeartRateZone.PUSH -> "PUSH IT! Double points baby!"
                HeartRateZone.PEAK -> "PEAK ZONE! MAXIMUM OVERDRIVE! Triple points!"
            }
        }
        speak(message)
    }

    fun onTimeUpdate(elapsedSeconds: Int) {
        if (!isInitialized) return
        val minutes = elapsedSeconds / 60
        if (minutes > 0 && minutes != lastSpokenMinute && minutes % 5 == 0) {
            lastSpokenMinute = minutes
            val message = when (currentStyle) {
                VoiceCoachStyle.LITERAL -> "$minutes minutes elapsed."
                VoiceCoachStyle.STANDARD -> "$minutes minutes in."
                VoiceCoachStyle.HYPE -> "$minutes minutes! Keep that energy up!"
            }
            speak(message)
        }
    }

    fun onMilestone(message: String) {
        if (!isInitialized) return
        speak(message)
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, text.hashCode().toString())
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        lastZone = null
        lastSpokenMinute = -1
    }
}
