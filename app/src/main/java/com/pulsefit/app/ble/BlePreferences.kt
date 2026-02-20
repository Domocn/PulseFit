package com.pulsefit.app.ble

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlePreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("ble_prefs", Context.MODE_PRIVATE)

    var lastDeviceAddress: String?
        get() = prefs.getString("last_device_address", null)
        set(value) = prefs.edit().putString("last_device_address", value).apply()

    var lastDeviceName: String?
        get() = prefs.getString("last_device_name", null)
        set(value) = prefs.edit().putString("last_device_name", value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }
}
