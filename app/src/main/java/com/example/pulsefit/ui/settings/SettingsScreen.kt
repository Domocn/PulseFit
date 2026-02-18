package com.example.pulsefit.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pulsefit.ui.theme.PulsePrimary
import com.example.pulsefit.ui.theme.PulseSurface

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val profile by viewModel.profile.collectAsState()
    val useSimulatedHr by viewModel.useSimulatedHr.collectAsState()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PulsePrimary,
        cursorColor = PulsePrimary,
        focusedLabelColor = PulsePrimary
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Profile section
        Text(
            text = "Profile",
            style = MaterialTheme.typography.titleMedium,
            color = PulsePrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = profile?.name ?: "",
            onValueChange = viewModel::updateName,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = profile?.age?.toString() ?: "",
            onValueChange = viewModel::updateAge,
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PulseSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Max Heart Rate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${profile?.maxHeartRate ?: "--"} bpm",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PulseSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ND Profile",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = profile?.ndProfile?.label ?: "Standard",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Daily target
        Text(
            text = "Daily Target",
            style = MaterialTheme.typography.titleMedium,
            color = PulsePrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${profile?.dailyTarget ?: 12} Burn Points",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Slider(
            value = (profile?.dailyTarget ?: 12).toFloat(),
            onValueChange = { viewModel.updateDailyTarget(it.toInt()) },
            valueRange = 5f..30f,
            steps = 24,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = PulsePrimary,
                activeTrackColor = PulsePrimary
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // HR Monitor section
        Text(
            text = "Heart Rate Monitor",
            style = MaterialTheme.typography.titleMedium,
            color = PulsePrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Use Simulated HR",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Generates realistic heart rate data for testing",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = useSimulatedHr,
                onCheckedChange = { viewModel.toggleSimulatedHr() },
                colors = SwitchDefaults.colors(checkedTrackColor = PulsePrimary)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = viewModel::save,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save Changes", color = PulsePrimary)
        }
    }
}
