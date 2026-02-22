package com.pulsefit.app.util

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import java.util.TimeZone

/**
 * Helper to sync challenges and weekly plans to the device calendar.
 * Requires WRITE_CALENDAR and READ_CALENDAR permissions (handled by caller).
 */
object CalendarSync {

    /**
     * Insert a calendar event for a challenge.
     * Returns the event ID, or null if insertion failed.
     */
    fun insertChallengeEvent(
        context: Context,
        title: String,
        description: String,
        startMillis: Long,
        endMillis: Long
    ): Long? {
        return try {
            val calendarId = getPrimaryCalendarId(context) ?: return null
            val values = ContentValues().apply {
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.TITLE, title)
                put(CalendarContract.Events.DESCRIPTION, description)
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                put(CalendarContract.Events.ALL_DAY, 1)
            }
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            uri?.lastPathSegment?.toLongOrNull()
        } catch (_: SecurityException) {
            null
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Insert a calendar event for a scheduled workout.
     * Returns the event ID, or null if insertion failed.
     */
    fun insertWorkoutEvent(
        context: Context,
        title: String,
        description: String,
        startMillis: Long,
        durationMinutes: Int
    ): Long? {
        return try {
            val calendarId = getPrimaryCalendarId(context) ?: return null
            val endMillis = startMillis + (durationMinutes * 60 * 1000L)
            val values = ContentValues().apply {
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.TITLE, title)
                put(CalendarContract.Events.DESCRIPTION, description)
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            uri?.lastPathSegment?.toLongOrNull()
        } catch (_: SecurityException) {
            null
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Delete a previously inserted calendar event.
     */
    fun deleteEvent(context: Context, eventId: Long): Boolean {
        return try {
            val uri = CalendarContract.Events.CONTENT_URI.buildUpon()
                .appendPath(eventId.toString())
                .build()
            context.contentResolver.delete(uri, null, null) > 0
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Get the primary (first writable) calendar ID on the device.
     */
    private fun getPrimaryCalendarId(context: Context): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.IS_PRIMARY,
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL
        )
        val selection = "${CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL} >= ?"
        val selectionArgs = arrayOf(CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR.toString())

        return try {
            context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idIndex = cursor.getColumnIndex(CalendarContract.Calendars._ID)
                    if (idIndex >= 0) cursor.getLong(idIndex) else null
                } else null
            }
        } catch (_: SecurityException) {
            null
        }
    }
}
