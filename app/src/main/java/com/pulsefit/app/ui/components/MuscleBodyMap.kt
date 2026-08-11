package com.pulsefit.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pulsefit.app.data.model.MuscleGroup
import com.pulsefit.app.domain.model.MuscleFatigue
import com.pulsefit.app.util.MuscleFatigueCalculator

/**
 * Interactive body map showing muscle group recovery status.
 * Front and back views with color-coded regions.
 *
 * Color coding:
 * - Green: Ready to train (fatigue < 20%)
 * - Lime: Slightly fatigued (20-40%)
 * - Orange: Fatigued (40-60%)
 * - Red: Very fatigued (60-80%)
 * - Dark red: Overtrained (80%+)
 */
@Composable
fun MuscleBodyMap(
    fatigueData: Map<String, MuscleFatigue>,
    selectedMuscle: String?,
    onMuscleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val calculator = MuscleFatigueCalculator()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(color = Color(0xFF22C55E), label = "Ready")
            LegendItem(color = Color(0xFF84CC16), label = "Slight")
            LegendItem(color = Color(0xFFF97316), label = "Fatigued")
            LegendItem(color = Color(0xFFEF4444), label = "Very")
            LegendItem(color = Color(0xFFDC2626), label = "Rest")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Front body
        Text(
            text = "Front",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))

        Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
            FrontBodyCanvas(
                fatigueData = fatigueData,
                selectedMuscle = selectedMuscle,
                onMuscleSelected = onMuscleSelected,
                calculator = calculator
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Back body
        Text(
            text = "Back",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))

        Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
            BackBodyCanvas(
                fatigueData = fatigueData,
                selectedMuscle = selectedMuscle,
                onMuscleSelected = onMuscleSelected,
                calculator = calculator
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color = color, radius = 5.dp.toPx())
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Front body view with muscle regions.
 */
@Composable
private fun FrontBodyCanvas(
    fatigueData: Map<String, MuscleFatigue>,
    selectedMuscle: String?,
    onMuscleSelected: (String) -> Unit,
    calculator: MuscleFatigueCalculator
) {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        // Body outline (simplified torso shape)
        val bodyOutline = Path().apply {
            // Head
            addOval(Rect(cx - 25, 10f, cx + 25, 65f))
            // Neck
            moveTo(cx - 12f, 60f)
            lineTo(cx - 20f, 80f)
            lineTo(cx + 20f, 80f)
            lineTo(cx + 12f, 60f)
            // Torso
            moveTo(cx - 20f, 80f)
            lineTo(cx - 35f, 200f)
            lineTo(cx - 30f, 260f)
            lineTo(cx + 30f, 260f)
            lineTo(cx + 35f, 200f)
            lineTo(cx + 20f, 80f)
            // Left arm
            moveTo(cx - 20f, 85f)
            lineTo(cx - 55f, 160f)
            lineTo(cx - 50f, 240f)
            // Right arm
            moveTo(cx + 20f, 85f)
            lineTo(cx + 55f, 160f)
            lineTo(cx + 50f, 240f)
            // Left leg
            moveTo(cx - 25f, 255f)
            lineTo(cx - 30f, 260f)
            lineTo(cx - 35f, 340f)
            lineTo(cx - 25f, 350f)
            // Right leg
            moveTo(cx + 25f, 255f)
            lineTo(cx + 30f, 260f)
            lineTo(cx + 35f, 340f)
            lineTo(cx + 25f, 350f)
        }
        drawPath(bodyOutline, color = onSurface, style = Stroke(width = 2.dp.toPx()))

        // Muscle regions (simplified rectangles for tappable areas)
        val regions = listOf(
            // Chest
            MuscleRegion("CHEST", Rect(cx - 30f, 85f, cx + 30f, 130f)),
            // Shoulders (left/right)
            MuscleRegion("SHOULDERS", Rect(cx - 55f, 80f, cx - 20f, 120f)),
            MuscleRegion("SHOULDERS", Rect(cx + 20f, 80f, cx + 55f, 120f)),
            // Biceps (left/right)
            MuscleRegion("BICEPS", Rect(cx - 55f, 120f, cx - 35f, 170f)),
            MuscleRegion("BICEPS", Rect(cx + 35f, 120f, cx + 55f, 170f)),
            // Forearms (left/right)
            MuscleRegion("FOREARMS", Rect(cx - 55f, 170f, cx - 35f, 240f)),
            MuscleRegion("FOREARMS", Rect(cx + 35f, 170f, cx + 55f, 240f)),
            // Core
            MuscleRegion("CORE", Rect(cx - 25f, 130f, cx + 25f, 200f)),
            // Quads (left/right)
            MuscleRegion("QUADS", Rect(cx - 35f, 260f, cx - 5f, 340f)),
            MuscleRegion("QUADS", Rect(cx + 5f, 260f, cx + 35f, 340f)),
            // Calves (left/right) — shown at bottom of legs
            MuscleRegion("CALVES", Rect(cx - 35f, 320f, cx - 5f, 350f)),
            MuscleRegion("CALVES", Rect(cx + 5f, 320f, cx + 35f, 350f)),
        )

        regions.forEach { region ->
            val fatigue = fatigueData[region.muscleGroup]
            val score = fatigue?.fatigueScore ?: 0f
            val status = calculator.getRecoveryStatus(score)
            val fillColor = Color(android.graphics.Color.parseColor(status.colorHex))
            val isSelected = selectedMuscle == region.muscleGroup

            drawRect(
                color = fillColor.copy(alpha = if (isSelected) 0.7f else 0.35f),
                topLeft = region.bounds.topLeft,
                size = region.bounds.size,
            )
            if (isSelected) {
                drawRect(
                    color = onSurface,
                    topLeft = region.bounds.topLeft,
                    size = region.bounds.size,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

/**
 * Back body view with muscle regions.
 */
@Composable
private fun BackBodyCanvas(
    fatigueData: Map<String, MuscleFatigue>,
    selectedMuscle: String?,
    onMuscleSelected: (String) -> Unit,
    calculator: MuscleFatigueCalculator
) {
    val onSurface = MaterialTheme.colorScheme.onSurface

    Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        // Body outline (back view — same silhouette)
        val bodyOutline = Path().apply {
            addOval(Rect(cx - 25, 10f, cx + 25, 65f))
            moveTo(cx - 12f, 60f); lineTo(cx - 20f, 80f); lineTo(cx + 20f, 80f); lineTo(cx + 12f, 60f)
            moveTo(cx - 20f, 80f); lineTo(cx - 35f, 200f); lineTo(cx - 30f, 260f); lineTo(cx + 30f, 260f); lineTo(cx + 35f, 200f); lineTo(cx + 20f, 80f)
            moveTo(cx - 20f, 85f); lineTo(cx - 55f, 160f); lineTo(cx - 50f, 240f)
            moveTo(cx + 20f, 85f); lineTo(cx + 55f, 160f); lineTo(cx + 50f, 240f)
            moveTo(cx - 25f, 255f); lineTo(cx - 30f, 260f); lineTo(cx - 35f, 340f); lineTo(cx - 25f, 350f)
            moveTo(cx + 25f, 255f); lineTo(cx + 30f, 260f); lineTo(cx + 35f, 340f); lineTo(cx + 25f, 350f)
        }
        drawPath(bodyOutline, color = onSurface, style = Stroke(width = 2.dp.toPx()))

        val regions = listOf(
            // Upper back / traps
            MuscleRegion("BACK", Rect(cx - 30f, 80f, cx + 30f, 120f)),
            // Lats (left/right)
            MuscleRegion("BACK", Rect(cx - 35f, 120f, cx - 5f, 180f)),
            MuscleRegion("BACK", Rect(cx + 5f, 120f, cx + 35f, 180f)),
            // Lower back
            MuscleRegion("LOWER_BACK", Rect(cx - 25f, 180f, cx + 25f, 220f)),
            // Shoulders rear (left/right)
            MuscleRegion("SHOULDERS", Rect(cx - 55f, 80f, cx - 20f, 120f)),
            MuscleRegion("SHOULDERS", Rect(cx + 20f, 80f, cx + 55f, 120f)),
            // Triceps (left/right)
            MuscleRegion("TRICEPS", Rect(cx - 55f, 120f, cx - 35f, 170f)),
            MuscleRegion("TRICEPS", Rect(cx + 35f, 120f, cx + 55f, 170f)),
            // Glutes (left/right)
            MuscleRegion("GLUTES", Rect(cx - 30f, 220f, cx - 5f, 260f)),
            MuscleRegion("GLUTES", Rect(cx + 5f, 220f, cx + 30f, 260f)),
            // Hamstrings (left/right)
            MuscleRegion("HAMSTRINGS", Rect(cx - 35f, 260f, cx - 5f, 310f)),
            MuscleRegion("HAMSTRINGS", Rect(cx + 5f, 260f, cx + 35f, 310f)),
            // Calves (left/right)
            MuscleRegion("CALVES", Rect(cx - 35f, 310f, cx - 5f, 350f)),
            MuscleRegion("CALVES", Rect(cx + 5f, 310f, cx + 35f, 350f)),
        )

        regions.forEach { region ->
            val fatigue = fatigueData[region.muscleGroup]
            val score = fatigue?.fatigueScore ?: 0f
            val status = calculator.getRecoveryStatus(score)
            val fillColor = Color(android.graphics.Color.parseColor(status.colorHex))
            val isSelected = selectedMuscle == region.muscleGroup

            drawRect(
                color = fillColor.copy(alpha = if (isSelected) 0.7f else 0.35f),
                topLeft = region.bounds.topLeft,
                size = region.bounds.size,
            )
            if (isSelected) {
                drawRect(
                    color = onSurface,
                    topLeft = region.bounds.topLeft,
                    size = region.bounds.size,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

private data class MuscleRegion(
    val muscleGroup: String,
    val bounds: Rect
)

/**
 * Compact muscle fatigue summary card for use in other screens.
 */
@Composable
fun MuscleFatigueSummary(
    fatigueData: List<MuscleFatigue>,
    modifier: Modifier = Modifier
) {
    val calculator = MuscleFatigueCalculator()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Muscle Recovery",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (fatigueData.isEmpty()) {
                Text(
                    text = "No training data yet. Complete a strength workout to see recovery status.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                fatigueData.take(6).forEach { fatigue ->
                    val status = calculator.getRecoveryStatus(fatigue.fatigueScore)
                    val statusColor = Color(android.graphics.Color.parseColor(status.colorHex))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fatigue.muscleGroup.replace("_", " ").lowercase()
                                .replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Canvas(modifier = Modifier.size(8.dp)) {
                                drawCircle(color = statusColor, radius = 4.dp.toPx())
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = status.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = statusColor
                            )
                        }
                    }
                }
            }
        }
    }
}
