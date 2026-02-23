package com.pulsefit.app.ui.ads

import android.app.Activity
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun WatchAdButton(
    adsRemaining: Int,
    adState: AdState,
    onWatchAd: (Activity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val dailyLimit = 5

    val canWatch = adsRemaining > 0 && adState == AdState.Ready && activity != null
    val isLoading = adState == AdState.Loading
    val limitReached = adsRemaining <= 0

    val buttonText = when {
        limitReached -> "Daily limit reached"
        isLoading -> "Loading ad..."
        adState == AdState.Showing -> "Watching ad..."
        canWatch -> "Watch Ad (+10 coins)"
        else -> "Loading ad..."
    }

    val statusText = "$adsRemaining ads remaining today (limit: $dailyLimit per day)"

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .semantics {
                    contentDescription = "Watch a short video ad to earn 10 Reward Coins. $statusText"
                }
        ) {
            Text(
                text = "Watch a short video ad to earn 10 Reward Coins",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { activity?.let { onWatchAd(it) } },
                enabled = canWatch,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(buttonText)
            }
        }
    }
}
