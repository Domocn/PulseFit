package com.example.pulsefit.ui.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

data class ShopItem(
    val id: String,
    val name: String,
    val description: String,
    val xpCost: Int,
    val category: String
)

@Composable
fun RewardShopScreen(viewModel: RewardShopViewModel = hiltViewModel()) {
    val totalXp by viewModel.totalXp.collectAsState()
    val ownedItems by viewModel.ownedItems.collectAsState()
    val streakShields by viewModel.streakShieldsOwned.collectAsState()

    val items = listOf(
        ShopItem("theme_ocean", "Ocean Theme", "Cool blue color palette", 500, "theme"),
        ShopItem("theme_forest", "Forest Theme", "Natural green tones", 500, "theme"),
        ShopItem("theme_sunset", "Sunset Theme", "Warm orange gradients", 500, "theme"),
        ShopItem("celebration_confetti", "Confetti Burst", "Extra confetti on workout complete", 300, "celebration"),
        ShopItem("celebration_fireworks", "Fireworks", "Fireworks celebration animation", 300, "celebration"),
        ShopItem("streak_shield", "Streak Shield", "One free streak miss", 1000, "utility")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Reward Shop",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Available XP: $totalXp",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        if (streakShields > 0) {
            Text(
                text = "Streak Shields: $streakShields",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                val isOwned = item.id in ownedItems && item.id != "streak_shield"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isOwned) {
                            Text(
                                text = "Owned",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        } else {
                            Button(
                                onClick = { viewModel.purchase(item) },
                                enabled = totalXp >= item.xpCost,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("${item.xpCost} XP")
                            }
                        }
                    }
                }
            }
        }
    }
}
