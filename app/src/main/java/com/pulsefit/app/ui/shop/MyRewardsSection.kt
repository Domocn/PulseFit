package com.pulsefit.app.ui.shop

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.pulsefit.app.data.remote.RedeemedCode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyRewardsSection(
    redeemedCodes: List<RedeemedCode>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        if (redeemedCodes.isEmpty()) {
            Text(
                text = "No redeemed codes yet. Watch ads to earn Reward Coins, then redeem discount codes from the Real Rewards tab.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            redeemedCodes.forEach { redeemed ->
                RedeemedCodeCard(
                    redeemed = redeemed,
                    onCopy = { code ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Discount Code", code))
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun RedeemedCodeCard(
    redeemed: RedeemedCode,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val redeemedDate = if (redeemed.redeemedAt > 0) {
        dateFormat.format(Date(redeemed.redeemedAt))
    } else ""

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${redeemed.brand} discount code: ${redeemed.code}. " +
                    "${redeemed.percentOff}% off. Redeemed on $redeemedDate. " +
                    "Tap copy button to copy code to clipboard."
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
                    text = redeemed.brand,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${redeemed.percentOff}% off",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = redeemed.code,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { onCopy(redeemed.code) }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy code to clipboard",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (redeemedDate.isNotEmpty()) {
                Text(
                    text = "Redeemed: $redeemedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (redeemed.expiryDate.isNotEmpty()) {
                Text(
                    text = "Expires: ${redeemed.expiryDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
