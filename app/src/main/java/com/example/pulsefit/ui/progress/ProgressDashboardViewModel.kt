package com.example.pulsefit.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.domain.model.UserProfile
import com.example.pulsefit.domain.model.WorkoutStats
import com.example.pulsefit.domain.repository.WorkoutRepository
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import com.example.pulsefit.domain.usecase.GetWorkoutStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class WeeklyBurnPoints(val weekLabel: String, val burnPoints: Int)

@HiltViewModel
class ProgressDashboardViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase,
    private val getWorkoutStats: GetWorkoutStatsUseCase,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _weeklyStats = MutableStateFlow<WorkoutStats?>(null)
    val weeklyStats: StateFlow<WorkoutStats?> = _weeklyStats

    private val _monthlyStats = MutableStateFlow<WorkoutStats?>(null)
    val monthlyStats: StateFlow<WorkoutStats?> = _monthlyStats

    private val _weeklyBpHistory = MutableStateFlow<List<WeeklyBurnPoints>>(emptyList())
    val weeklyBpHistory: StateFlow<List<WeeklyBurnPoints>> = _weeklyBpHistory

    private val _zoneDistribution = MutableStateFlow<Map<HeartRateZone, Long>>(emptyMap())
    val zoneDistribution: StateFlow<Map<HeartRateZone, Long>> = _zoneDistribution

    private val _targetHitRate = MutableStateFlow(0f)
    val targetHitRate: StateFlow<Float> = _targetHitRate

    init {
        viewModelScope.launch {
            _weeklyStats.value = getWorkoutStats.getWeeklyStats()
            _monthlyStats.value = getWorkoutStats.getMonthlyStats()
            loadWeeklyBpHistory()
            loadZoneDistribution()
            loadTargetHitRate()
        }
    }

    private suspend fun loadWeeklyBpHistory() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val weeks = mutableListOf<WeeklyBurnPoints>()

        for (i in 7 downTo 0) {
            val weekStart = today.minusWeeks(i.toLong())
                .minusDays((today.minusWeeks(i.toLong()).dayOfWeek.value - 1).toLong())
            val weekEnd = weekStart.plusDays(7)
            val startMillis = weekStart.atStartOfDay(zone).toInstant().toEpochMilli()
            val endMillis = weekEnd.atStartOfDay(zone).toInstant().toEpochMilli()

            val workouts = workoutRepository.getWorkoutsInDateRange(startMillis, endMillis)
            val totalBp = workouts.sumOf { it.burnPoints }
            val label = "${weekStart.monthValue}/${weekStart.dayOfMonth}"
            weeks.add(WeeklyBurnPoints(label, totalBp))
        }

        _weeklyBpHistory.value = weeks
    }

    private suspend fun loadZoneDistribution() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val startMillis = today.minusDays(30).atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        val workouts = workoutRepository.getWorkoutsInDateRange(startMillis, endMillis)
        val aggregated = mutableMapOf<HeartRateZone, Long>()
        for (w in workouts) {
            for ((z, seconds) in w.zoneTime) {
                aggregated[z] = (aggregated[z] ?: 0L) + seconds
            }
        }
        _zoneDistribution.value = aggregated
    }

    private suspend fun loadTargetHitRate() {
        val profile = getUserProfile.once() ?: return
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val startMillis = today.minusDays(30).atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        val workouts = workoutRepository.getWorkoutsInDateRange(startMillis, endMillis)
        if (workouts.isEmpty()) return
        val hits = workouts.count { it.burnPoints >= profile.dailyTarget }
        _targetHitRate.value = hits.toFloat() / workouts.size
    }
}
