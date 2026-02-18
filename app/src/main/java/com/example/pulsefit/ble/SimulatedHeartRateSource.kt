package com.example.pulsefit.ble

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sin
import kotlin.random.Random

class SimulatedHeartRateSource @Inject constructor() : HeartRateSource {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null
    private var tick = 0

    private val _heartRate = MutableStateFlow<Int?>(null)
    override val heartRate: StateFlow<Int?> = _heartRate

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    override val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus

    override fun connect(deviceAddress: String?) {
        _connectionStatus.value = ConnectionStatus.CONNECTING
        job = scope.launch {
            delay(500)
            _connectionStatus.value = ConnectionStatus.CONNECTED
            _isConnected.value = true
            tick = 0

            while (isActive) {
                val baseHr = generateRealisticHr(tick)
                val noise = Random.nextInt(-2, 3)
                _heartRate.value = (baseHr + noise).coerceIn(55, 195)
                tick++
                delay(1000)
            }
        }
    }

    override fun disconnect() {
        job?.cancel()
        job = null
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
        _isConnected.value = false
        _heartRate.value = null
    }

    private fun generateRealisticHr(tick: Int): Int {
        // Cycle through phases: rest → warmup → active → push → peak → cooldown
        // Full cycle ~5 minutes (300 ticks)
        val cyclePosition = tick % 300
        return when {
            cyclePosition < 30 -> 70  // Rest
            cyclePosition < 60 -> {
                // Warm up: 70 → 110
                val progress = (cyclePosition - 30) / 30.0
                (70 + 40 * progress).toInt()
            }
            cyclePosition < 120 -> {
                // Active: 110 → 140 with variation
                val progress = (cyclePosition - 60) / 60.0
                val variation = sin(cyclePosition * 0.3) * 5
                (110 + 30 * progress + variation).toInt()
            }
            cyclePosition < 180 -> {
                // Push: 140 → 165
                val progress = (cyclePosition - 120) / 60.0
                val variation = sin(cyclePosition * 0.2) * 4
                (140 + 25 * progress + variation).toInt()
            }
            cyclePosition < 210 -> {
                // Peak: 165 → 180
                val progress = (cyclePosition - 180) / 30.0
                (165 + 15 * progress).toInt()
            }
            else -> {
                // Cooldown: 180 → 70
                val progress = (cyclePosition - 210) / 90.0
                (180 - 110 * progress).toInt()
            }
        }
    }
}
