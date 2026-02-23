package com.pulsefit.app.ui.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.pulsefit.app.data.remote.DiscountCode

@Composable
fun DiscountCodeCard(
    code: DiscountCode,
    userCoins: Int,
    onRedeem: (DiscountCode) -> Unit,
    modifier: Modifier = Modifier
) {
    val canAfford = userCoins >= code.coinCost
    val coinsNeeded = code.coinCost - userCoins

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${code.brand}: ${code.percentOff}% off. " +
                    "${code.description}. Costs ${code.coinCost} Reward Coins. " +
                    if (canAfford) "You can afford this." else "Need $coinsNeeded more coins."
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = code.brand,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${code.percentOff}% off",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = code.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (code.expiryDate.isNotEmpty()) {
                Text(
                    text = "Expires: ${code.expiryDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onRedeem(code) },
                enabled = canAfford,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (canAfford) {
                        "Redeem (${code.coinCost} coins)"
                    } else {
                        "Need $coinsNeeded more coins"
                    }
                )
            }
        }
    }
}
