package com.example.pulsefit.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PulsePrimary,
    secondary = PulseSecondary,
    background = PulseBackground,
    surface = PulseSurface,
    surfaceVariant = PulseSurfaceVariant,
    onBackground = PulseOnBackground,
    onSurface = PulseOnSurface,
    onSurfaceVariant = PulseOnSurfaceVariant,
    error = PulseError,
    onPrimary = PulseOnBackground,
    onSecondary = PulseOnBackground,
    onError = PulseOnBackground
)

@Composable
fun PulseFitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
