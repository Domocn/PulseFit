package com.pulsefit.app.ui.plan

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.exercise.WeeklyPlanGenerator
import com.pulsefit.app.data.model.PlannedDay
import com.pulsefit.app.data.model.PlannedDayType
import com.pulsefit.app.data.model.WeeklyPlan
import com.pulsefit.app.domain.repository.UserRepository
import com.pulsefit.app.util.CalendarSync
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class WeeklyPlanViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val weeklyPlanGenerator: WeeklyPlanGenerator
) : ViewModel() {

    private val _weeklyPlan = MutableStateFlow<WeeklyPlan?>(null)
    val weeklyPlan: StateFlow<WeeklyPlan?> = _weeklyPlan

    private val _hasEquipmentProfile = MutableStateFlow(false)
    val hasEquipmentProfile: StateFlow<Boolean> = _hasEquipmentProfile

    private val _calendarSynced = MutableStateFlow(false)
    val calendarSynced: StateFlow<Boolean> = _calendarSynced

    init {
        generatePlan()
    }

    fun generatePlan() {
        viewModelScope.launch {
            val profile = userRepository.getUserProfileOnce()
            if (profile == null) {
                _hasEquipmentProfile.value = false
                return@launch
            }
            val equipProfile = profile.getEquipmentProfile()
            _hasEquipmentProfile.value = profile.equipmentProfileJson != null

            val weekStart = getWeekStartMillis()
            _weeklyPlan.value = weeklyPlanGenerator.generate(equipProfile, weekStart)
        }
    }

    fun syncToCalendar(context: Context) {
        val plan = _weeklyPlan.value ?: return
        viewModelScope.launch {
            var anyInserted = false
            val updatedDays = plan.days.map { day ->
                if (day.type == PlannedDayType.WORKOUT && day.templateName != null) {
                    val dayOffset = day.dayOfWeek.ordinal
                    val startMillis = plan.weekStartDate + (dayOffset * 24 * 60 * 60 * 1000L)

                    // Set to 7 AM by default
                    val cal = Calendar.getInstance().apply {
                        timeInMillis = startMillis
                        set(Calendar.HOUR_OF_DAY, 7)
                        set(Calendar.MINUTE, 0)
                    }

                    val eventId = CalendarSync.insertWorkoutEvent(
                        context = context,
                        title = "PulseFit: ${day.templateName}",
                        description = "Focus: ${day.focus}\nDuration: ${day.durationMinutes} min",
                        startMillis = cal.timeInMillis,
                        durationMinutes = day.durationMinutes
                    )
                    if (eventId != null) anyInserted = true
                    day.copy(calendarEventId = eventId)
                } else day
            }
            _weeklyPlan.value = plan.copy(days = updatedDays)
            _calendarSynced.value = anyInserted
        }
    }

    private fun getWeekStartMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
