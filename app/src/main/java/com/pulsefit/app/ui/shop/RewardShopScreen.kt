package com.pulsefit.app.ui.shop

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pulsefit.app.ui.ads.WatchAdButton
import java.text.NumberFormat

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
    val rewardCoins by viewModel.rewardCoins.collectAsState()
    val ownedItems by viewModel.ownedItems.collectAsState()
    val streakShields by viewModel.streakShieldsOwned.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val adsRemaining by viewModel.adsRemaining.collectAsState()
    val discountCodes by viewModel.discountCodes.collectAsState()
    val redeemedCodes by viewModel.redeemedCodes.collectAsState()
    val adState by viewModel.rewardedAdManager.adState.collectAsState()
    val redeemError by viewModel.redeemError.collectAsState()

    val context = LocalContext.current
    val activity = context as? Activity

    // Preload ad when screen opens
    LaunchedEffect(activity) {
        activity?.let { viewModel.rewardedAdManager.loadAd(it) }
    }

    val numberFormat = NumberFormat.getNumberInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {
        Text(
            text = "Reward Shop",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Available XP: ${numberFormat.format(totalXp)}. Reward Coins: ${numberFormat.format(rewardCoins)}"
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "XP: ${numberFormat.format(totalXp)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Coins: ${numberFormat.format(rewardCoins)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        if (streakShields > 0) {
            Text(
                text = "Streak Shields: $streakShields",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val tabTitles = listOf("Cosmetics", "Real Rewards", "My Rewards")
        TabRow(selectedTabIndex = selectedTab) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { viewModel.selectTab(index) },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Error snackbar
        if (redeemError != null) {
            Snackbar(
                action = {
                    TextButton(onClick = { viewModel.clearRedeemError() }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(redeemError ?: "")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        when (selectedTab) {
            0 -> CosmeticsTab(
                totalXp = totalXp,
                ownedItems = ownedItems,
                onPurchase = { viewModel.purchase(it) }
            )
            1 -> RealRewardsTab(
                adsRemaining = adsRemaining,
                adState = adState,
                rewardCoins = rewardCoins.toInt(),
                discountCodes = discountCodes,
                onWatchAd = { viewModel.watchAd(it) },
                onRedeem = { viewModel.redeemDiscountCode(it) }
            )
            2 -> MyRewardsSection(redeemedCodes = redeemedCodes)
        }
    }
}

@Composable
private fun CosmeticsTab(
    totalXp: Long,
    ownedItems: Set<String>,
    onPurchase: (ShopItem) -> Unit
) {
    val items = listOf(
        ShopItem("theme_ocean", "Ocean Theme", "Cool blue color palette", 500, "theme"),
        ShopItem("theme_forest", "Forest Theme", "Natural green tones", 500, "theme"),
        ShopItem("theme_sunset", "Sunset Theme", "Warm orange gradients", 500, "theme"),
        ShopItem("celebration_confetti", "Confetti Burst", "Extra confetti on workout complete", 300, "celebration"),
        ShopItem("celebration_fireworks", "Fireworks", "Fireworks celebration animation", 300, "celebration"),
        ShopItem("streak_shield", "Streak Shield", "One free streak miss", 1000, "utility")
    )

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
                            onClick = { onPurchase(item) },
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

@Composable
private fun RealRewardsTab(
    adsRemaining: Int,
    adState: com.pulsefit.app.ui.ads.AdState,
    rewardCoins: Int,
    discountCodes: List<com.pulsefit.app.data.remote.DiscountCode>,
    onWatchAd: (Activity) -> Unit,
    onRedeem: (com.pulsefit.app.data.remote.DiscountCode) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            WatchAdButton(
                adsRemaining = adsRemaining,
                adState = adState,
                onWatchAd = onWatchAd
            )
        }

        if (discountCodes.isEmpty()) {
            item {
                Text(
                    text = "No discount codes available right now. Check back later for partner deals on fitness gear, supplements, and more.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(discountCodes) { code ->
                DiscountCodeCard(
                    code = code,
                    userCoins = rewardCoins,
                    onRedeem = onRedeem
                )
            }
        }
    }
}
