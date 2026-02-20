package com.pulsefit.app.ui.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.domain.model.Workout
import com.pulsefit.app.domain.model.WorkoutStats
import com.pulsefit.app.domain.repository.WorkoutRepository
import com.pulsefit.app.domain.usecase.GetWorkoutStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.sqrt

enum class DateFilter(val label: String, val days: Int) {
    WEEK("7d", 7),
    TWO_WEEKS("14d", 14),
    MONTH("30d", 30),
    QUARTER("90d", 90)
}

data class HrStats(
    val meanHr: Int,
    val medianHr: Int,
    val stdDevHr: Int
)

@HiltViewModel
class DeepDataViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val getWorkoutStats: GetWorkoutStatsUseCase
) : ViewModel() {

    private val _filteredWorkouts = MutableStateFlow<List<Workout>>(emptyList())
    val filteredWorkouts: StateFlow<List<Workout>> = _filteredWorkouts

    private val _stats = MutableStateFlow<WorkoutStats?>(null)
    val stats: StateFlow<WorkoutStats?> = _stats

    private val _hrStats = MutableStateFlow<HrStats?>(null)
    val hrStats: StateFlow<HrStats?> = _hrStats

    private val _selectedFilter = MutableStateFlow(DateFilter.MONTH)
    val selectedFilter: StateFlow<DateFilter> = _selectedFilter

    private val _compareA = MutableStateFlow<Workout?>(null)
    val compareA: StateFlow<Workout?> = _compareA

    private val _compareB = MutableStateFlow<Workout?>(null)
    val compareB: StateFlow<Workout?> = _compareB

    init {
        loadData(DateFilter.MONTH)
    }

    fun setFilter(filter: DateFilter) {
        _selectedFilter.value = filter
        loadData(filter)
    }

    fun toggleCompare(workout: Workout) {
        when {
            _compareA.value?.id == workout.id -> _compareA.value = null
            _compareB.value?.id == workout.id -> _compareB.value = null
            _compareA.value == null -> _compareA.value = workout
            _compareB.value == null -> _compareB.value = workout
            else -> {
                _compareA.value = workout
                _compareB.value = null
            }
        }
    }

    fun clearComparison() {
        _compareA.value = null
        _compareB.value = null
    }

    private fun loadData(filter: DateFilter) {
        viewModelScope.launch {
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            val startMillis = today.minusDays(filter.days.toLong()).atStartOfDay(zone).toInstant().toEpochMilli()
            val endMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

            val workouts = workoutRepository.getWorkoutsInDateRange(startMillis, endMillis)
                .sortedByDescending { it.startTime }
            _filteredWorkouts.value = workouts

            if (workouts.isNotEmpty()) {
                _stats.value = WorkoutStats(
                    totalWorkouts = workouts.size,
                    totalBurnPoints = workouts.sumOf { it.burnPoints },
                    totalDurationSeconds = workouts.sumOf { it.durationSeconds },
                    averageBurnPoints = workouts.sumOf { it.burnPoints } / workouts.size,
                    averageDurationSeconds = workouts.sumOf { it.durationSeconds } / workouts.size
                )

                // Compute HR stats
                val hrValues = workouts.filter { it.averageHeartRate > 0 }.map { it.averageHeartRate }
                if (hrValues.isNotEmpty()) {
                    val mean = hrValues.average().toInt()
                    val sorted = hrValues.sorted()
                    val median = if (sorted.size % 2 == 0) {
                        (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2
                    } else {
                        sorted[sorted.size / 2]
                    }
                    val variance = hrValues.map { (it - mean).toDouble() * (it - mean) }.average()
                    val stdDev = sqrt(variance).toInt()
                    _hrStats.value = HrStats(mean, median, stdDev)
                }
            } else {
                _stats.value = null
                _hrStats.value = null
            }
        }
    }
}
