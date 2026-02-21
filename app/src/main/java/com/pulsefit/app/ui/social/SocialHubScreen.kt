package com.pulsefit.app.ui.social

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SocialHubScreen(
    onNavigateToFriends: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToAccountability: () -> Unit = {},
    onNavigateToGroups: () -> Unit = {},
    viewModel: SocialHubViewModel = hiltViewModel()
) {
    val isShielded by viewModel.isShielded.collectAsState()
    val friendCount by viewModel.friendCount.collectAsState()
    val pendingCount by viewModel.pendingCount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Social",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isShielded) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Social Pressure Shield is on",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Social features are hidden to reduce pressure. You can change this in Sensory Settings.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return
        }

        SocialNavCard(
            icon = Icons.Default.People,
            title = "Friends",
            subtitle = "$friendCount friends" + if (pendingCount > 0) " \u2022 $pendingCount pending" else "",
            badgeCount = pendingCount,
            onClick = onNavigateToFriends
        )

        Spacer(modifier = Modifier.height(8.dp))

        SocialNavCard(
            icon = Icons.Default.Leaderboard,
            title = "Leaderboard",
            subtitle = "Weekly burn points ranking",
            onClick = onNavigateToLeaderboard
        )

        Spacer(modifier = Modifier.height(8.dp))

        SocialNavCard(
            icon = Icons.Default.DynamicFeed,
            title = "Activity Feed",
            subtitle = "See your friends' workouts",
            onClick = onNavigateToFeed
        )

        Spacer(modifier = Modifier.height(8.dp))

        SocialNavCard(
            icon = Icons.Default.Handshake,
            title = "Accountability",
            subtitle = "Partner up for weekly goals",
            onClick = onNavigateToAccountability
        )

        Spacer(modifier = Modifier.height(8.dp))

        SocialNavCard(
            icon = Icons.Default.Groups,
            title = "Groups",
            subtitle = "Family & team workout events",
            onClick = onNavigateToGroups
        )
    }
}

@Composable
private fun SocialNavCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (badgeCount > 0) {
                BadgedBox(badge = {
                    Badge { Text(badgeCount.toString()) }
                }) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
