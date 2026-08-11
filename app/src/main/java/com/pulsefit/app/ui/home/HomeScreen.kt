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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
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
import com.pulsefit.app.data.model.GamificationLevel
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
    onNavigateToChallenges: (() -> Unit)? = null,
    onNavigateToWeeklyPlan: (() -> Unit)? = null,
    onNavigateToRecovery: (() -> Unit)? = null,
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
    val readiness by viewModel.readiness.collectAsState()
    val adjustedTarget by viewModel.adjustedTarget.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val showEnergyDialog by viewModel.showEnergyDialog.collectAsState()
    val gamificationLevel by viewModel.gamificationLevel.collectAsState()
    val spoonBudget by viewModel.spoonBudget.collectAsState()
    val spoonBudgetEnabled by viewModel.spoonBudgetEnabled.collectAsState()
    val pdaMode by viewModel.pdaMode.collectAsState()
    val decisionResult by viewModel.decisionResult.collectAsState()
    val busyPrediction by viewModel.busyPrediction.collectAsState()
    val currentMicro by viewModel.currentMicro.collectAsState()

    if (showEnergyDialog) {
        EnergyCheckDialog(
            onDismiss = viewModel::dismissEnergyCheck,
            onEnergySelected = viewModel::onEnergySelected
        )
    }

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
                    text = viewModel.pdaTransform("Ready to earn some burn points?"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ConnectionStatusIndicator(status = connectionStatus)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Streak + Level row (hidden in MINIMAL/OFF gamification)
        if ((gamificationLevel == GamificationLevel.FULL || gamificationLevel == GamificationLevel.MODERATE) &&
            (currentStreak > 0 || (profile?.xpLevel ?: 1) > 1)) {
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

        // Spoon Budget (F7)
        if (spoonBudgetEnabled && spoonBudget != null) {
            SpoonBudgetCard(
                dailySpoons = spoonBudget!!.dailySpoons,
                usedSpoons = spoonBudget!!.usedSpoons
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Gym Busyness (F15)
        busyPrediction?.let { prediction ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Gym: ${prediction.label}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = prediction.suggestion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
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

        // Readiness score card
        readiness?.let { r ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        r.score >= 60 -> MaterialTheme.colorScheme.surface
                        r.score >= 40 -> MaterialTheme.colorScheme.surfaceVariant
                        else -> MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Readiness: ${r.score}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = r.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    r.factors.forEach { factor ->
                        Text(
                            text = "${factor.name}: ${factor.status}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (r.score < 20) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rest day recommended - your body needs recovery",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Adjusted target card (sleep-aware scaling)
        adjustedTarget?.let { scaled ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Today's target: ${scaled.adjustedTarget} burn points",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = scaled.reason,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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

        // Recovery link (F6/F12) when readiness is low
        if (shouldRest || (readiness != null && readiness!!.score < 30)) {
            onNavigateToRecovery?.let {
                OutlinedButton(
                    onClick = it,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = viewModel.pdaTransform("Need something gentle?"),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // GO button
        Button(
            onClick = viewModel::onStartWorkout,
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = viewModel.pdaTransform("GO"),
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

        Spacer(modifier = Modifier.height(12.dp))

        // Energy check button
        OutlinedButton(
            onClick = viewModel::showEnergyCheck,
            modifier = Modifier.height(40.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "How are you feeling?",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Decide for Me (F2)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = viewModel::decideForMe,
            modifier = Modifier.height(40.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = viewModel.pdaTransform("Decide for Me"),
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Decision result
        decisionResult?.let { decision ->
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = decision.template.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "${decision.template.durationMinutes} min - ${decision.reason}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        // Workout recommendations (shown after energy selection)
        if (recommendations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Recommended for you",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            recommendations.forEach { rec ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = rec.template.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${rec.template.durationMinutes} min - ${rec.reason}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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

        // Micro Workout card (F14)
        currentMicro?.let { micro ->
            MicroWorkoutCard(
                microWorkout = micro,
                onComplete = viewModel::completeMicro,
                onShuffle = viewModel::shuffleMicro
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Weekly goal card
        WeeklyGoalCard(currentWorkouts = weeklyWorkouts)

        Spacer(modifier = Modifier.height(16.dp))

        // Daily quests card (hidden in MINIMAL/OFF gamification)
        if (gamificationLevel == GamificationLevel.FULL || gamificationLevel == GamificationLevel.MODERATE) {
            DailyQuestsCard(quests = dailyQuests)
        }

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

        Spacer(modifier = Modifier.height(8.dp))

        // Second nav row: Challenges + Weekly Plan
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            onNavigateToChallenges?.let {
                QuickNavCard(
                    icon = Icons.Default.EmojiEvents,
                    label = "Challenges",
                    onClick = it,
                    modifier = Modifier.weight(1f)
                )
            }
            onNavigateToWeeklyPlan?.let {
                QuickNavCard(
                    icon = Icons.Default.CalendarMonth,
                    label = "My Plan",
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
