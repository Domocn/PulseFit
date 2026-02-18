package com.example.pulsefit.ble

import kotlinx.coroutines.flow.StateFlow

enum class ConnectionStatus {
    DISCONNECTED, SCANNING, CONNECTING, CONNECTED
}

interface HeartRateSource {
    val heartRate: StateFlow<Int?>
    val isConnected: StateFlow<Boolean>
    val connectionStatus: StateFlow<ConnectionStatus>
    fun connect(deviceAddress: String? = null)
    fun disconnect()
}
