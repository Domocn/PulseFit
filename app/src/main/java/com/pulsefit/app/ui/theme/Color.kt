package com.pulsefit.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.pulsefit.app.data.model.AppTheme

data class PulsePalette(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val secondary: Color,
    val onBackground: Color = Color(0xFFFAFAFA),
    val onSurface: Color = Color(0xFFF4F4F5),
    val onSurfaceVariant: Color = Color(0xFFA1A1AA),
    val error: Color = Color(0xFFEF4444)
)

val MidnightPalette = PulsePalette(
    background = Color(0xFF121218),
    surface = Color(0xFF1E1E2E),
    surfaceVariant = Color(0xFF2D2D3F),
    primary = Color(0xFF8B5CF6),
    secondary = Color(0xFF10B981)
)

val EmberPalette = PulsePalette(
    background = Color(0xFF1A1210),
    surface = Color(0xFF2A1F1A),
    surfaceVariant = Color(0xFF3D2E24),
    primary = Color(0xFFF97316),
    secondary = Color(0xFFFBBF24)
)

val OceanPalette = PulsePalette(
    background = Color(0xFF0C1620),
    surface = Color(0xFF132233),
    surfaceVariant = Color(0xFF1B3148),
    primary = Color(0xFF06B6D4),
    secondary = Color(0xFF3B82F6)
)

val BerryPalette = PulsePalette(
    background = Color(0xFF1A0E1A),
    surface = Color(0xFF2C1A2E),
    surfaceVariant = Color(0xFF3F2640),
    primary = Color(0xFFEC4899),
    secondary = Color(0xFFA855F7)
)

val ForestPalette = PulsePalette(
    background = Color(0xFF0E1A12),
    surface = Color(0xFF1A2C1E),
    surfaceVariant = Color(0xFF243F2A),
    primary = Color(0xFF22C55E),
    secondary = Color(0xFF84CC16)
)

val SlatePalette = PulsePalette(
    background = Color(0xFF141618),
    surface = Color(0xFF1E2124),
    surfaceVariant = Color(0xFF2C3035),
    primary = Color(0xFF64748B),
    secondary = Color(0xFF94A3B8)
)

fun AppTheme.toPalette(): PulsePalette = when (this) {
    AppTheme.MIDNIGHT -> MidnightPalette
    AppTheme.EMBER -> EmberPalette
    AppTheme.OCEAN -> OceanPalette
    AppTheme.BERRY -> BerryPalette
    AppTheme.FOREST -> ForestPalette
    AppTheme.SLATE -> SlatePalette
}

// Zone colors (HR-based, not theme-dependent)
val ZoneRest = Color(0xFF71717A)
val ZoneWarmUp = Color(0xFF3B82F6)
val ZoneActive = Color(0xFF22C55E)
val ZonePush = Color(0xFFF97316)
val ZonePeak = Color(0xFFEF4444)
