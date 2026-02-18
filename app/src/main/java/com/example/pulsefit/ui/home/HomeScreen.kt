package com.example.pulsefit.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pulsefit.ui.components.BurnPointsRing
import com.example.pulsefit.ui.components.ConnectionStatusIndicator
import com.example.pulsefit.ui.components.StatCard
import com.example.pulsefit.ui.theme.PulsePrimary

@Composable
fun HomeScreen(
    onStartWorkout: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val profile by viewModel.userProfile.collectAsState()
    val todayPoints by viewModel.todayBurnPoints.collectAsState()
    val workoutId by viewModel.workoutId.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()

    LaunchedEffect(workoutId) {
        workoutId?.let {
            onStartWorkout(it)
            viewModel.onWorkoutNavigated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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

        Spacer(modifier = Modifier.height(32.dp))

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

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = viewModel::onStartWorkout,
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = PulsePrimary)
        ) {
            Text(
                text = "GO",
                fontSize = 32.sp,
                style = MaterialTheme.typography.displaySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(label = "Max HR", value = "${profile?.maxHeartRate ?: "--"}")
            StatCard(label = "Zone Target", value = "${profile?.dailyTarget ?: 12} pts")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
