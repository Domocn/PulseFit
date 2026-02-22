package com.pulsefit.app.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.pulsefit.app.BuildConfig
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
        private const val MAX_RECONNECT_ATTEMPTS = 3
        private const val RECONNECT_DELAY_MS = 2000L
        val HR_SERVICE_UUID: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        val HR_MEASUREMENT_UUID: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
        val CLIENT_CONFIG_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    private var gatt: BluetoothGatt? = null
    private var lastDeviceAddress: String? = null
    private var reconnectAttempts = 0
    private var intentionalDisconnect = false
    private val handler = Handler(Looper.getMainLooper())

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
                    if (BuildConfig.DEBUG) Log.d(TAG, "Connected to GATT server")
                    _connectionStatus.value = ConnectionStatus.CONNECTED
                    _isConnected.value = true
                    reconnectAttempts = 0
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    if (BuildConfig.DEBUG) Log.d(TAG, "Disconnected from GATT server")
                    _isConnected.value = false
                    _heartRate.value = null
                    gatt?.close()
                    // Auto-reconnect if not intentional
                    if (!intentionalDisconnect && lastDeviceAddress != null && reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                        reconnectAttempts++
                        _connectionStatus.value = ConnectionStatus.CONNECTING
                        if (BuildConfig.DEBUG) Log.d(TAG, "Auto-reconnect attempt $reconnectAttempts/$MAX_RECONNECT_ATTEMPTS")
                        handler.postDelayed({
                            attemptReconnect()
                        }, RECONNECT_DELAY_MS)
                    } else {
                        _connectionStatus.value = ConnectionStatus.DISCONNECTED
                    }
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
        lastDeviceAddress = deviceAddress
        reconnectAttempts = 0
        intentionalDisconnect = false
        val device = bluetoothAdapter?.getRemoteDevice(deviceAddress) ?: return
        _connectionStatus.value = ConnectionStatus.CONNECTING
        gatt = device.connectGatt(context, true, gattCallback)
    }

    override fun disconnect() {
        intentionalDisconnect = true
        handler.removeCallbacksAndMessages(null)
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
        _isConnected.value = false
        _heartRate.value = null
    }

    private fun attemptReconnect() {
        val address = lastDeviceAddress ?: return
        val device = bluetoothAdapter?.getRemoteDevice(address) ?: return
        gatt = device.connectGatt(context, true, gattCallback)
    }
}
