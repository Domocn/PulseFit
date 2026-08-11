package com.pulsefit.app.ui.history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * GitHub-style contribution heatmap showing workout consistency over the past year.
 *
 * Each cell represents one day. Color intensity maps to workout volume/duration:
 *   Level 0 (empty): No workout
 *   Level 1 (light): 1-25% of max
 *   Level 2 (medium): 26-50% of max
 *   Level 3 (strong): 51-75% of max
 *   Level 4 (intense): 76-100% of max
 */
@Composable
fun CalendarHeatmap(
    dailyActivity: Map<LocalDate, Float>, // date -> activity value (volume kg or duration mins)
    onDayClick: (LocalDate) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    // Show ~52 weeks back, starting from last Sunday
    val endDate = today
    val startDate = endDate.minusWeeks(52).with(DayOfWeek.SUNDAY)

    val maxActivity = dailyActivity.values.maxOrNull() ?: 1f

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Activity",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${dailyActivity.size} workouts in the past year",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Day labels (Mon, Wed, Fri)
            Row(modifier = Modifier.fillMaxWidth()) {
                // Label column
                Column(
                    modifier = Modifier.width(28.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Empty for alignment
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("M", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("W", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("F", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Heatmap grid
                Column {
                    // Month labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        var lastMonth = -1
                        var colIndex = 0
                        var date = startDate
                        while (!date.isAfter(endDate)) {
                            val month = date.monthValue
                            if (month != lastMonth) {
                                Text(
                                    text = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.width(14.dp * (7 - colIndex % 7).coerceAtMost(7))
                                )
                                lastMonth = month
                            }
                            colIndex++
                            date = date.plusDays(1)
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // 7 rows (Sun-Sat), columns = weeks
                    val totalDays = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
                    val totalWeeks = (totalDays + 6) / 7

                    for (dayOfWeek in 0 until 7) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            for (week in 0 until totalWeeks) {
                                val dayIndex = week * 7 + dayOfWeek
                                val date = startDate.plusDays(dayIndex.toLong())

                                if (date.isAfter(endDate)) {
                                    // Future day — empty cell
                                    Box(modifier = Modifier.size(14.dp))
                                } else if (date.isAfter(today)) {
                                    // Future day in current week — empty
                                    Box(modifier = Modifier.size(14.dp))
                                } else {
                                    val activity = dailyActivity[date] ?: 0f
                                    val level = when {
                                        activity <= 0f -> 0
                                        activity <= maxActivity * 0.25f -> 1
                                        activity <= maxActivity * 0.50f -> 2
                                        activity <= maxActivity * 0.75f -> 3
                                        else -> 4
                                    }

                                    val cellColor = heatmapColor(level)

                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(cellColor)
                                            .clickable { onDayClick(date) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Less",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                for (level in 0..4) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(heatmapColor(level))
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "More",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun heatmapColor(level: Int): Color {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val primary = MaterialTheme.colorScheme.primary
    return when (level) {
        0 -> surfaceVariant.copy(alpha = 0.3f)
        1 -> primary.copy(alpha = 0.2f)
        2 -> primary.copy(alpha = 0.45f)
        3 -> primary.copy(alpha = 0.7f)
        4 -> primary
        else -> surfaceVariant.copy(alpha = 0.3f)
    }
}
