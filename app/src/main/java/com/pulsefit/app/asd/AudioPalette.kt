package com.pulsefit.app.asd

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sin

/**
 * F144: Fixed sound mapping for ASD profiles.
 * Every event always produces the same sound, no variation.
 */
@Singleton
class AudioPalette @Inject constructor() {

    private val playScope = CoroutineScope(Dispatchers.IO)

    enum class SoundEvent {
        ZONE_UP,       // rising tone
        ZONE_DOWN,     // falling tone
        POINT_EARNED,  // soft click
        MILESTONE,     // chime
        WORKOUT_START, // bell
        WORKOUT_END    // completion tone
    }

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

    fun play(event: SoundEvent) {
        val frequency = toneForEvent(event)
        val durationMs = durationForEvent(event)
        playScope.launch {
            playTone(frequency, durationMs)
        }
    }

    private fun playTone(frequencyHz: Int, durationMs: Int) {
        val sampleRate = 44100
        val numSamples = (sampleRate * durationMs / 1000.0).toInt()
        val samples = ShortArray(numSamples)
        val amplitude = 0.25 // Keep volume moderate for ASD comfort

        for (i in samples.indices) {
            val fade = if (i < sampleRate / 50) i.toDouble() / (sampleRate / 50)
            else if (i > numSamples - sampleRate / 50) (numSamples - i).toDouble() / (sampleRate / 50)
            else 1.0
            samples[i] = (Short.MAX_VALUE * amplitude * fade *
                    sin(2.0 * Math.PI * frequencyHz * i / sampleRate)).toInt().toShort()
        }

        val bufferSize = samples.size * 2
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(samples, 0, samples.size)
        audioTrack.play()
        Thread.sleep(durationMs.toLong() + 50)
        audioTrack.stop()
        audioTrack.release()
    }
}
