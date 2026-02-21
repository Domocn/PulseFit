package com.pulsefit.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.ui.components.BurnPointsRing
import com.pulsefit.app.ui.components.ConnectionStatusIndicator
import com.pulsefit.app.ui.components.StatCard

@Composable
fun HomeScreen(
    onStartWorkout: (Long) -> Unit,
    onNavigateToTemplates: (() -> Unit)? = null,
    onNavigateToProgress: (() -> Unit)? = null,
    onNavigateToShop: (() -> Unit)? = null,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val profile by viewModel.userProfile.collectAsState()
    val todayPoints by viewModel.todayBurnPoints.collectAsState()
    val workoutId by viewModel.workoutId.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    val weeklyWorkouts by viewModel.weeklyWorkouts.collectAsState()
    val weeklyBurnPoints by viewModel.weeklyBurnPoints.collectAsState()
    val dailyQuests by viewModel.dailyQuests.collectAsState()
    val shouldRest by viewModel.shouldRest.collectAsState()
    val avgBurnPoints by viewModel.avgBurnPoints.collectAsState()
    val weeklyTheme by viewModel.weeklyTheme.collectAsState()
    val daysSinceLastWorkout by viewModel.daysSinceLastWorkout.collectAsState()

    LaunchedEffect(workoutId) {
        workoutId?.let {
            onStartWorkout(it)
            viewModel.onWorkoutNavigated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hey, ${profile?.name ?: ""}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Ready to earn some burn points?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ConnectionStatusIndicator(status = connectionStatus)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Streak + Level row
        if (currentStreak > 0 || (profile?.xpLevel ?: 1) > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (currentStreak > 0) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = "$currentStreak day streak",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$currentStreak day streak",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
                profile?.let { p ->
                    if (p.xpLevel > 1) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Level ${p.xpLevel}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Rest day suggestion (Anti-Burnout System)
        if (shouldRest) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Rest Day Suggested",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "You've been training hard this week. A rest day helps recovery.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Weekly theme card (ADHD novelty feature)
        weeklyTheme?.let { theme ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "This Week's Vibe",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = theme,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Points estimate (ASD comfort feature)
        val ndProf = profile?.ndProfile
        if ((ndProf == NdProfile.ASD || ndProf == NdProfile.AUDHD) && avgBurnPoints > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Expected Points",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Based on your average, you'll earn ~$avgBurnPoints points per workout",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Growth Garden (ADHD progress visualization)
        profile?.let { p ->
            GrowthGardenCard(
                totalBurnPoints = p.totalBurnPoints,
                daysSinceLastWorkout = daysSinceLastWorkout
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        BurnPointsRing(
            current = todayPoints,
            target = profile?.dailyTarget ?: 12,
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "$todayPoints / ${profile?.dailyTarget ?: 12}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Burn Points today",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // GO button
        Button(
            onClick = viewModel::onStartWorkout,
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "GO",
                fontSize = 32.sp,
                style = MaterialTheme.typography.displaySmall
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Just 5 Min button (ADHD feature)
        val ndProfile = profile?.ndProfile
        if (ndProfile == NdProfile.ADHD || ndProfile == NdProfile.AUDHD || ndProfile == NdProfile.STANDARD) {
            OutlinedButton(
                onClick = viewModel::onStartJustFiveMin,
                modifier = Modifier.height(44.dp),
                shape = RoundedCornerShape(22.dp)
            ) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = "Quick 5 minute workout",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Just 5 Minutes",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Weekly stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(label = "This Week", value = "$weeklyWorkouts workouts")
            StatCard(label = "Weekly Pts", value = "$weeklyBurnPoints")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weekly goal card
        WeeklyGoalCard(currentWorkouts = weeklyWorkouts)

        Spacer(modifier = Modifier.height(16.dp))

        // Daily quests card
        DailyQuestsCard(quests = dailyQuests)

        Spacer(modifier = Modifier.height(16.dp))

        // Quick navigation row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            onNavigateToTemplates?.let {
                QuickNavCard(
                    icon = Icons.Default.FitnessCenter,
                    label = "Templates",
                    onClick = it,
                    modifier = Modifier.weight(1f)
                )
            }
            onNavigateToProgress?.let {
                QuickNavCard(
                    icon = Icons.Default.BarChart,
                    label = "Progress",
                    onClick = it,
                    modifier = Modifier.weight(1f)
                )
            }
            onNavigateToShop?.let {
                QuickNavCard(
                    icon = Icons.Default.Stars,
                    label = "Rewards",
                    onClick = it,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun QuickNavCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick, role = Role.Button),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
