package com.example.pulsefit.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.pulsefit.data.model.AppTheme

val LocalPulsePalette = staticCompositionLocalOf { MidnightPalette }

@Composable
fun PulseFitTheme(
    appTheme: AppTheme = AppTheme.MIDNIGHT,
    content: @Composable () -> Unit
) {
    val palette = appTheme.toPalette()

    val colorScheme = darkColorScheme(
        primary = palette.primary,
        secondary = palette.secondary,
        background = palette.background,
        surface = palette.surface,
        surfaceVariant = palette.surfaceVariant,
        onBackground = palette.onBackground,
        onSurface = palette.onSurface,
        onSurfaceVariant = palette.onSurfaceVariant,
        error = palette.error,
        onPrimary = palette.onBackground,
        onSecondary = palette.onBackground,
        onError = palette.onBackground
    )

    CompositionLocalProvider(LocalPulsePalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
