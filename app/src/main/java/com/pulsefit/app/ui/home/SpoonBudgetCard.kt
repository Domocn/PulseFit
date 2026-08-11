package com.pulsefit.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun SpoonBudgetCard(
    dailySpoons: Int,
    usedSpoons: Float,
    modifier: Modifier = Modifier
) {
    val remaining = (dailySpoons - usedSpoons).coerceAtLeast(0f)
    val fraction = if (dailySpoons > 0) (remaining / dailySpoons) else 1f

    Card(
        modifier = modifier.fillMaxWidth().semantics {
            contentDescription = "Spoon budget: ${remaining.toInt()} of $dailySpoons spoons remaining"
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Energy Budget", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Text("${remaining.toInt()} / $dailySpoons", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = when {
                    fraction > 0.5f -> MaterialTheme.colorScheme.primary
                    fraction > 0.25f -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.error
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            if (remaining < 3f) {
                Spacer(Modifier.height(4.dp))
                Text("Low energy today. Recovery workouts recommended.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
