package com.pulsefit.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.domain.model.Workout
import com.pulsefit.app.domain.repository.WorkoutRepository
import com.pulsefit.app.domain.usecase.GetUserProfileUseCase
import com.pulsefit.app.domain.usecase.GetWorkoutHistoryUseCase
import com.pulsefit.app.ui.history.components.DayStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getWorkoutHistory: GetWorkoutHistoryUseCase,
    private val workoutRepository: WorkoutRepository,
    private val getUserProfile: GetUserProfileUseCase
) : ViewModel() {

    val workouts: StateFlow<List<Workout>> = getWorkoutHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth

    private val _dayStatuses = MutableStateFlow<Map<Int, DayStatus>>(emptyMap())
    val dayStatuses: StateFlow<Map<Int, DayStatus>> = _dayStatuses

    private val _weeklyTrend = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val weeklyTrend: StateFlow<List<Pair<String, Int>>> = _weeklyTrend

    private var dailyTarget = 12

    init {
        viewModelScope.launch {
            val profile = getUserProfile.once()
            dailyTarget = profile?.dailyTarget ?: 12
            loadMonthData()
            loadWeeklyTrend()
        }
    }

    fun previousMonth() {
        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
        viewModelScope.launch { loadMonthData() }
    }

    fun nextMonth() {
        _currentYearMonth.value = _currentYearMonth.value.plusMonths(1)
        viewModelScope.launch { loadMonthData() }
    }

    private suspend fun loadMonthData() {
        val ym = _currentYearMonth.value
        val zone = ZoneId.systemDefault()
        val startMillis = ym.atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = ym.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()

        val workouts = workoutRepository.getWorkoutsInDateRange(startMillis, endMillis)

        // Group by day of month, sum burn points
        val dayPoints = mutableMapOf<Int, Int>()
        workouts.forEach { w ->
            val day = w.startTime.atZone(zone).dayOfMonth
            dayPoints[day] = (dayPoints[day] ?: 0) + w.burnPoints
        }

        val statuses = dayPoints.mapValues { (_, points) ->
            when {
                points >= dailyTarget -> DayStatus.TARGET_HIT
                points > 0 -> DayStatus.PARTIAL
                else -> DayStatus.NONE
            }
        }

        _dayStatuses.value = statuses
    }

    private suspend fun loadWeeklyTrend() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val data = mutableListOf<Pair<String, Int>>()

        // Last 7 weeks
        for (i in 6 downTo 0) {
            val weekStart = today.minusWeeks(i.toLong()).with(java.time.DayOfWeek.MONDAY)
            val weekEnd = weekStart.plusDays(7)
            val startMillis = weekStart.atStartOfDay(zone).toInstant().toEpochMilli()
            val endMillis = weekEnd.atStartOfDay(zone).toInstant().toEpochMilli()

            val workouts = workoutRepository.getWorkoutsInDateRange(startMillis, endMillis)
            val totalPoints = workouts.sumOf { it.burnPoints }
            val label = "${weekStart.monthValue}/${weekStart.dayOfMonth}"
            data.add(label to totalPoints)
        }

        _weeklyTrend.value = data
    }
}
