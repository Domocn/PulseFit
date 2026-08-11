package com.pulsefit.app.util

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarBlocker @Inject constructor() {

    data class TimeSlot(
        val startMillis: Long,
        val endMillis: Long,
        val label: String
    )

    fun findOpenSlots(
        context: Context,
        date: Calendar,
        preferredHours: List<Int> = listOf(6, 7, 12, 17, 18),
        durationMinutes: Int = 30
    ): List<TimeSlot> {
        val busySlots = getBusySlots(context, date)
        val slots = mutableListOf<TimeSlot>()

        for (hour in preferredHours) {
            val start = (date.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            val end = (start.clone() as Calendar).apply {
                add(Calendar.MINUTE, durationMinutes)
            }
            val overlaps = busySlots.any { busy ->
                start.timeInMillis < busy.second && end.timeInMillis > busy.first
            }
            if (!overlaps) {
                slots.add(TimeSlot(start.timeInMillis, end.timeInMillis, "${hour}:00"))
            }
        }
        return slots
    }

    fun blockTime(context: Context, slot: TimeSlot, calendarId: Long): Long? {
        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.TITLE, "PulseFit: ${slot.label}")
            put(CalendarContract.Events.DTSTART, slot.startMillis)
            put(CalendarContract.Events.DTEND, slot.endMillis)
            put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().timeZone.id)
            put(CalendarContract.Events.DESCRIPTION, "Workout time blocked by PulseFit")
        }
        val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        return uri?.lastPathSegment?.toLongOrNull()
    }

    private fun getBusySlots(context: Context, date: Calendar): List<Pair<Long, Long>> {
        val dayStart = (date.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        }.timeInMillis
        val dayEnd = (date.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
        }.timeInMillis

        val projection = arrayOf(
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        )
        val selection = "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?"
        val selectionArgs = arrayOf(dayStart.toString(), dayEnd.toString())

        val slots = mutableListOf<Pair<Long, Long>>()
        try {
            context.contentResolver.query(
                CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val start = cursor.getLong(0)
                    val end = cursor.getLong(1)
                    slots.add(start to end)
                }
            }
        } catch (_: SecurityException) {
            // Calendar permission not granted
        }
        return slots
    }
}
