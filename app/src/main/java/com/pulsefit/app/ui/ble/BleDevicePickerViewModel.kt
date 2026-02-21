package com.pulsefit.app.ui.ble

import androidx.lifecycle.ViewModel
import com.pulsefit.app.ble.BleDevice
import com.pulsefit.app.ble.BlePreferences
import com.pulsefit.app.ble.BleScanner
import com.pulsefit.app.ble.HeartRateSource
import com.pulsefit.app.ble.RealHeartRate
import com.pulsefit.app.ble.SimulatedHeartRate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BleDevicePickerViewModel @Inject constructor(
    private val bleScanner: BleScanner,
    @RealHeartRate private val realHeartRateSource: HeartRateSource,
    @SimulatedHeartRate private val simulatedHeartRateSource: HeartRateSource,
    private val blePreferences: BlePreferences
) : ViewModel() {

    val devices: StateFlow<List<BleDevice>> = bleScanner.devices
    val isScanning: StateFlow<Boolean> = bleScanner.isScanning

    fun startScan() {
        bleScanner.startScan()
    }

    fun stopScan() {
        bleScanner.stopScan()
    }

    fun connectToDevice(address: String) {
        bleScanner.stopScan()
        blePreferences.lastDeviceAddress = address
        val device = devices.value.find { it.address == address }
        blePreferences.lastDeviceName = device?.name
        blePreferences.useSimulatedHr = false
        realHeartRateSource.connect(address)
    }

    fun useSimulated() {
        bleScanner.stopScan()
        blePreferences.useSimulatedHr = true
        simulatedHeartRateSource.connect(null)
    }

    override fun onCleared() {
        super.onCleared()
        bleScanner.stopScan()
    }
}
