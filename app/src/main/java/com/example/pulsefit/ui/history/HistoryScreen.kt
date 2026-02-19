package com.example.pulsefit.ui.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pulsefit.ui.history.components.CalendarView
import com.example.pulsefit.ui.history.components.TrendChart
import com.example.pulsefit.ui.history.components.WorkoutCard

@Composable
fun HistoryScreen(
    onWorkoutClick: (Long) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val workouts by viewModel.workouts.collectAsState()
    val currentYearMonth by viewModel.currentYearMonth.collectAsState()
    val dayStatuses by viewModel.dayStatuses.collectAsState()
    val weeklyTrend by viewModel.weeklyTrend.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        item {
            Text(
                text = "History",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Calendar
        item {
            CalendarView(
                yearMonth = currentYearMonth,
                dayStatuses = dayStatuses,
                onPreviousMonth = viewModel::previousMonth,
                onNextMonth = viewModel::nextMonth,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Trend chart
        item {
            TrendChart(
                weeklyData = weeklyTrend,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (workouts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No workouts yet. Tap GO to get started!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(workouts, key = { it.id }) { workout ->
                WorkoutCard(
                    workout = workout,
                    onClick = { onWorkoutClick(workout.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
