package com.example.pulsefit.ui.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.local.dao.WeeklyRoutineDao
import com.example.pulsefit.data.local.entity.WeeklyRoutineEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineBuilderViewModel @Inject constructor(
    private val weeklyRoutineDao: WeeklyRoutineDao
) : ViewModel() {

    val routines: StateFlow<List<WeeklyRoutineEntity>> = weeklyRoutineDao.getAllRoutines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addDefaultRoutine() {
        viewModelScope.launch {
            val existing = routines.value
            val nextDay = if (existing.isEmpty()) 1 else {
                val usedDays = existing.map { it.dayOfWeek }.toSet()
                (1..7).firstOrNull { it !in usedDays } ?: 1
            }
            weeklyRoutineDao.insertOrUpdate(
                WeeklyRoutineEntity(
                    dayOfWeek = nextDay,
                    timeHour = 18,
                    timeMinute = 0,
                    durationMinutes = 30
                )
            )
        }
    }

    fun updateRoutine(id: Long, dayOfWeek: Int, timeHour: Int, timeMinute: Int, durationMinutes: Int) {
        viewModelScope.launch {
            weeklyRoutineDao.insertOrUpdate(
                WeeklyRoutineEntity(
                    id = id,
                    dayOfWeek = dayOfWeek,
                    timeHour = timeHour,
                    timeMinute = timeMinute,
                    durationMinutes = durationMinutes
                )
            )
        }
    }

    fun deleteRoutine(id: Long) {
        viewModelScope.launch {
            weeklyRoutineDao.deleteById(id)
        }
    }
}
