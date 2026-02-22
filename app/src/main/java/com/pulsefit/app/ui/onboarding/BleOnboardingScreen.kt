package com.pulsefit.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pulsefit.app.ui.ble.BleDevicePickerSheet

@Composable
fun BleOnboardingScreen(
    onNext: () -> Unit
) {
    var showBlePicker by remember { mutableStateOf(false) }
    var deviceConnected by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Heart Rate Monitor",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Connect a Bluetooth LE heart rate monitor for the best experience. You can also use simulated data for testing.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (deviceConnected) {
            Text(
                text = "Device connected",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = { showBlePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Scan for Devices")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (deviceConnected) "Next" else "Skip for Now")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You can connect a device anytime in Settings",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showBlePicker) {
        BleDevicePickerSheet(
            onDismiss = { showBlePicker = false },
            onDeviceSelected = {
                deviceConnected = true
                showBlePicker = false
            },
            onUseSimulated = {
                showBlePicker = false
            }
        )
    }
}
