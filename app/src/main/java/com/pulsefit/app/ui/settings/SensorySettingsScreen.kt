package com.pulsefit.app.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pulsefit.app.data.model.AnimationLevel
import com.pulsefit.app.data.model.CelebrationStyle
import com.pulsefit.app.data.model.ColourIntensity
import com.pulsefit.app.data.model.HapticLevel
import com.pulsefit.app.data.model.SoundLevel
import com.pulsefit.app.data.model.VoiceCoachStyle

@Composable
fun SensorySettingsScreen(viewModel: SensorySettingsViewModel = hiltViewModel()) {
    val prefs by viewModel.preferences.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Sensory Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Animation Level
        SensorySlider(
            label = "Animations",
            values = AnimationLevel.entries.map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } },
            selectedIndex = prefs?.animationLevel?.ordinal ?: 2,
            onValueChange = { viewModel.updateAnimationLevel(AnimationLevel.entries[it]) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sound Level
        SensorySlider(
            label = "Sound",
            values = SoundLevel.entries.map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } },
            selectedIndex = prefs?.soundLevel?.ordinal ?: 2,
            onValueChange = { viewModel.updateSoundLevel(SoundLevel.entries[it]) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Haptic Level
        SensorySlider(
            label = "Haptics",
            values = HapticLevel.entries.map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } },
            selectedIndex = prefs?.hapticLevel?.ordinal ?: 2,
            onValueChange = { viewModel.updateHapticLevel(HapticLevel.entries[it]) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Colour Intensity
        SensorySlider(
            label = "Colour Intensity",
            values = ColourIntensity.entries.map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } },
            selectedIndex = prefs?.colourIntensity?.ordinal ?: 1,
            onValueChange = { viewModel.updateColourIntensity(ColourIntensity.entries[it]) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Celebration Style
        SensorySlider(
            label = "Celebrations",
            values = CelebrationStyle.entries.map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } },
            selectedIndex = prefs?.celebrationStyle?.ordinal ?: 1,
            onValueChange = { viewModel.updateCelebrationStyle(CelebrationStyle.entries[it]) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Voice Coach Style
        SensorySlider(
            label = "Voice Coach",
            values = VoiceCoachStyle.entries.map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } },
            selectedIndex = prefs?.voiceCoachStyle?.ordinal ?: 1,
            onValueChange = { viewModel.updateVoiceCoachStyle(VoiceCoachStyle.entries[it]) }
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { viewModel.previewVoiceStyle() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    Icons.Default.VolumeUp,
                    contentDescription = "Preview voice",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Preview",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Minimal Mode Toggle
        ToggleRow(
            label = "Minimal Mode",
            description = "Stripped-down workout UI with large text",
            checked = prefs?.minimalMode ?: false,
            onCheckedChange = { viewModel.updateMinimalMode(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // UI Lock Toggle
        ToggleRow(
            label = "Lock UI Layout",
            description = "Prevent layout changes during workout",
            checked = prefs?.uiLocked ?: false,
            onCheckedChange = { viewModel.updateUiLocked(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Social Pressure Shield
        ToggleRow(
            label = "Social Pressure Shield",
            description = "Hide all comparative and social elements",
            checked = prefs?.socialPressureShield ?: false,
            onCheckedChange = { viewModel.updateSocialPressureShield(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SensorySlider(
    label: String,
    values: List<String>,
    selectedIndex: Int,
    onValueChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = values.getOrElse(selectedIndex) { "" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        val currentValue = values.getOrElse(selectedIndex) { "" }
        Slider(
            value = selectedIndex.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..(values.size - 1).toFloat(),
            steps = values.size - 2,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "$label slider"
                    stateDescription = "$currentValue, ${selectedIndex + 1} of ${values.size}"
                },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun ToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
        )
    }
}
