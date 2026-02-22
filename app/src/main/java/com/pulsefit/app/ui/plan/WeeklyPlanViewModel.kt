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
import com.pulsefit.app.util.DeviceCalendar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _syncError = MutableStateFlow<String?>(null)
    val syncError: StateFlow<String?> = _syncError

    private val _availableCalendars = MutableStateFlow<List<DeviceCalendar>>(emptyList())
    val availableCalendars: StateFlow<List<DeviceCalendar>> = _availableCalendars

    private val _selectedCalendarId = MutableStateFlow<Long?>(null)
    val selectedCalendarId: StateFlow<Long?> = _selectedCalendarId

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

    fun loadCalendars(context: Context) {
        viewModelScope.launch {
            val calendars = withContext(Dispatchers.IO) {
                CalendarSync.getWritableCalendars(context)
            }
            _availableCalendars.value = calendars
        }
    }

    fun selectCalendar(calendarId: Long) {
        _selectedCalendarId.value = calendarId
    }

    fun syncToCalendar(context: Context) {
        val plan = _weeklyPlan.value ?: return
        val calId = _selectedCalendarId.value
        _syncError.value = null
        _calendarSynced.value = false

        viewModelScope.launch {
            var insertedCount = 0
            var failedCount = 0

            val updatedDays = withContext(Dispatchers.IO) {
                plan.days.map { day ->
                    if (day.type == PlannedDayType.WORKOUT && day.templateName != null) {
                        val dayOffset = day.dayOfWeek.ordinal
                        val dayMillis = plan.weekStartDate + (dayOffset * 24 * 60 * 60 * 1000L)

                        val cal = Calendar.getInstance().apply {
                            timeInMillis = dayMillis
                            set(Calendar.HOUR_OF_DAY, 7)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }

                        val eventId = CalendarSync.insertWorkoutEvent(
                            context = context,
                            title = "PulseFit: ${day.templateName}",
                            description = "Focus: ${day.focus}\nDuration: ${day.durationMinutes} min",
                            startMillis = cal.timeInMillis,
                            durationMinutes = day.durationMinutes,
                            calendarId = calId
                        )
                        if (eventId != null) insertedCount++ else failedCount++
                        day.copy(calendarEventId = eventId)
                    } else day
                }
            }

            _weeklyPlan.value = plan.copy(days = updatedDays)

            if (insertedCount > 0) {
                _calendarSynced.value = true
                _syncError.value = null
            } else if (failedCount > 0) {
                _syncError.value = "Could not add events. Check that you have a calendar account set up on this device."
            } else {
                _syncError.value = "No workouts to sync."
            }
        }
    }

    fun removeDay(context: Context, dayIndex: Int) {
        val plan = _weeklyPlan.value ?: return
        if (dayIndex !in plan.days.indices) return

        val day = plan.days[dayIndex]
        viewModelScope.launch {
            // Delete calendar event if synced
            if (day.calendarEventId != null) {
                withContext(Dispatchers.IO) {
                    CalendarSync.deleteEvent(context, day.calendarEventId)
                }
            }

            // Replace with a rest day
            val updatedDays = plan.days.toMutableList()
            updatedDays[dayIndex] = PlannedDay(
                dayOfWeek = day.dayOfWeek,
                type = PlannedDayType.REST,
                focus = "Rest & recover"
            )
            _weeklyPlan.value = plan.copy(days = updatedDays)
        }
    }

    fun clearSyncError() {
        _syncError.value = null
    }

    private fun getWeekStartMillis(): Long {
        val cal = Calendar.getInstance()
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (dayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - dayOfWeek
        cal.add(Calendar.DAY_OF_YEAR, daysFromMonday)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
