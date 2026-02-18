package com.example.pulsefit.ui.ble

import androidx.lifecycle.ViewModel
import com.example.pulsefit.ble.BleDevice
import com.example.pulsefit.ble.BleScanner
import com.example.pulsefit.ble.HeartRateSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BleDevicePickerViewModel @Inject constructor(
    private val bleScanner: BleScanner,
    private val heartRateSource: HeartRateSource
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
        heartRateSource.connect(address)
    }

    fun useSimulated() {
        bleScanner.stopScan()
        heartRateSource.connect(null)
    }

    override fun onCleared() {
        super.onCleared()
        bleScanner.stopScan()
    }
}
