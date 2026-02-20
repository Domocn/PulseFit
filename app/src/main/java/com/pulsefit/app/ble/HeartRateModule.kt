package com.pulsefit.app.ble

import android.bluetooth.BluetoothManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RealHeartRate

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SimulatedHeartRate

@Module
@InstallIn(SingletonComponent::class)
object HeartRateModule {

    @Provides
    @Singleton
    @RealHeartRate
    fun provideRealHeartRateSource(@ApplicationContext context: Context): HeartRateSource {
        return BleHeartRateSource(context)
    }

    @Provides
    @Singleton
    @SimulatedHeartRate
    fun provideSimulatedHeartRateSource(): HeartRateSource {
        return SimulatedHeartRateSource()
    }

    @Provides
    @Singleton
    fun provideHeartRateSource(@ApplicationContext context: Context): HeartRateSource {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        return if (bluetoothManager?.adapter != null) {
            BleHeartRateSource(context)
        } else {
            SimulatedHeartRateSource()
        }
    }
}
