package com.pulsefit.app.ui.plan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.util.CalendarBlocker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CalendarBlockViewModel @Inject constructor(
    application: Application,
    private val calendarBlocker: CalendarBlocker
) : AndroidViewModel(application) {

    private val _openSlots = MutableStateFlow<List<CalendarBlocker.TimeSlot>>(emptyList())
    val openSlots: StateFlow<List<CalendarBlocker.TimeSlot>> = _openSlots

    private val _blockedMessage = MutableStateFlow<String?>(null)
    val blockedMessage: StateFlow<String?> = _blockedMessage

    fun findSlots(durationMinutes: Int = 30) {
        viewModelScope.launch {
            val slots = calendarBlocker.findOpenSlots(
                getApplication(), Calendar.getInstance(), durationMinutes = durationMinutes
            )
            _openSlots.value = slots
        }
    }

    fun blockSlot(slot: CalendarBlocker.TimeSlot, calendarId: Long = 1L) {
        viewModelScope.launch {
            val eventId = calendarBlocker.blockTime(getApplication(), slot, calendarId)
            _blockedMessage.value = if (eventId != null) "Workout time blocked on your calendar" else "Could not block time. Check calendar permissions."
        }
    }

    fun dismissMessage() { _blockedMessage.value = null }
}
