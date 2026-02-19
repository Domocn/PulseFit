package com.example.pulsefit.ui.ble

import androidx.lifecycle.ViewModel
import com.example.pulsefit.ble.BleDevice
import com.example.pulsefit.ble.BlePreferences
import com.example.pulsefit.ble.BleScanner
import com.example.pulsefit.ble.HeartRateSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BleDevicePickerViewModel @Inject constructor(
    private val bleScanner: BleScanner,
    private val heartRateSource: HeartRateSource,
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
        heartRateSource.connect(address)
    }

    fun useSimulated() {
        bleScanner.stopScan()
        blePreferences.lastDeviceAddress = null
        blePreferences.lastDeviceName = null
        heartRateSource.connect(null)
    }

    override fun onCleared() {
        super.onCleared()
        bleScanner.stopScan()
    }
}
