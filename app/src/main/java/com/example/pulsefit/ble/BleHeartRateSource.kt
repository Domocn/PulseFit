package com.example.pulsefit.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BleHeartRateSource @Inject constructor(
    @ApplicationContext private val context: Context
) : HeartRateSource {

    companion object {
        private const val TAG = "BleHeartRateSource"
        val HR_SERVICE_UUID: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        val HR_MEASUREMENT_UUID: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
        val CLIENT_CONFIG_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    private var gatt: BluetoothGatt? = null

    private val _heartRate = MutableStateFlow<Int?>(null)
    override val heartRate: StateFlow<Int?> = _heartRate

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    override val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "Connected to GATT server")
                    _connectionStatus.value = ConnectionStatus.CONNECTED
                    _isConnected.value = true
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Disconnected from GATT server")
                    _connectionStatus.value = ConnectionStatus.DISCONNECTED
                    _isConnected.value = false
                    _heartRate.value = null
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val hrService = gatt.getService(HR_SERVICE_UUID)
                val hrChar = hrService?.getCharacteristic(HR_MEASUREMENT_UUID)
                if (hrChar != null) {
                    gatt.setCharacteristicNotification(hrChar, true)
                    val descriptor = hrChar.getDescriptor(CLIENT_CONFIG_UUID)
                    if (descriptor != null) {
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(descriptor)
                    }
                }
            }
        }

        @Deprecated("Deprecated in API 33, but needed for older APIs")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == HR_MEASUREMENT_UUID) {
                val flag = characteristic.value[0].toInt()
                val hr = if (flag and 0x01 != 0) {
                    // 16-bit heart rate
                    (characteristic.value[1].toInt() and 0xFF) or
                            ((characteristic.value[2].toInt() and 0xFF) shl 8)
                } else {
                    // 8-bit heart rate
                    characteristic.value[1].toInt() and 0xFF
                }
                _heartRate.value = hr
            }
        }
    }

    override fun connect(deviceAddress: String?) {
        if (deviceAddress == null) return
        val device = bluetoothAdapter?.getRemoteDevice(deviceAddress) ?: return
        _connectionStatus.value = ConnectionStatus.CONNECTING
        gatt = device.connectGatt(context, true, gattCallback)
    }

    override fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
        _isConnected.value = false
        _heartRate.value = null
    }
}
