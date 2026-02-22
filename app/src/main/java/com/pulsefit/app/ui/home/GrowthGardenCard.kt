package com.pulsefit.app.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class GrowthStage(val label: String, val icon: String, val minPoints: Long, val maxPoints: Long) {
    SEED("Seed", "\uD83C\uDF31", 0, 49),
    SPROUT("Sprout", "\uD83C\uDF3F", 50, 199),
    SAPLING("Sapling", "\uD83C\uDF3E", 200, 499),
    TREE("Tree", "\uD83C\uDF33", 500, 999),
    FLOWERING("Flowering", "\uD83C\uDF38", 1000, Long.MAX_VALUE);

    companion object {
        fun fromPoints(points: Long): GrowthStage {
            return entries.lastOrNull { points >= it.minPoints } ?: SEED
        }
    }
}

@Composable
fun GrowthGardenCard(
    totalBurnPoints: Long,
    daysSinceLastWorkout: Int,
    modifier: Modifier = Modifier
) {
    val isWilted = daysSinceLastWorkout >= 3
    val stage = GrowthStage.fromPoints(totalBurnPoints)

    val progress = if (stage == GrowthStage.FLOWERING) {
        1f
    } else {
        val range = stage.maxPoints - stage.minPoints + 1
        ((totalBurnPoints - stage.minPoints).toFloat() / range).coerceIn(0f, 1f)
    }

    val nextStage = GrowthStage.entries.getOrNull(stage.ordinal + 1)

    val gardenDescription = if (isWilted) {
        "Growth Garden: ${stage.label}, wilted. Water your garden by working out today. $totalBurnPoints total burn points"
    } else {
        val nextInfo = if (nextStage != null) ", ${stage.maxPoints + 1 - totalBurnPoints} points to ${nextStage.label}" else ""
        "Growth Garden: ${stage.label}. $totalBurnPoints total burn points$nextInfo"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = gardenDescription },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isWilted) "\uD83E\uDD40" else stage.icon,
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isWilted) "${stage.label} (Wilted)" else stage.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isWilted) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isWilted) "Water your garden — work out today!"
                        else "Your Growth Garden",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (isWilted) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (nextStage != null) "$totalBurnPoints / ${stage.maxPoints + 1} to ${nextStage.label}"
                else "$totalBurnPoints total burn points",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
