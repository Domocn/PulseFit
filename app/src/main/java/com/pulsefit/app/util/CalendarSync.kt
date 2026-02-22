package com.pulsefit.app.util

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import java.util.TimeZone

/**
 * Helper to sync challenges and weekly plans to the device calendar.
 * Requires WRITE_CALENDAR and READ_CALENDAR permissions (handled by caller).
 */
/**
 * Represents a calendar account on the device.
 */
data class DeviceCalendar(
    val id: Long,
    val displayName: String,
    val accountName: String,
    val accountType: String
)

object CalendarSync {

    private const val TAG = "CalendarSync"

    /**
     * Get all writable calendars on the device for the user to pick from.
     */
    fun getWritableCalendars(context: Context): List<DeviceCalendar> {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE,
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL
        )

        return try {
            val calendars = mutableListOf<DeviceCalendar>()
            context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val idCol = cursor.getColumnIndex(CalendarContract.Calendars._ID)
                val nameCol = cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
                val accountCol = cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)
                val typeCol = cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE)
                val accessCol = cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL)

                if (idCol < 0 || accessCol < 0) return emptyList()

                while (cursor.moveToNext()) {
                    val access = cursor.getInt(accessCol)
                    if (access < CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR) continue

                    calendars.add(DeviceCalendar(
                        id = cursor.getLong(idCol),
                        displayName = if (nameCol >= 0) cursor.getString(nameCol) ?: "" else "",
                        accountName = if (accountCol >= 0) cursor.getString(accountCol) ?: "" else "",
                        accountType = if (typeCol >= 0) cursor.getString(typeCol) ?: "" else ""
                    ))
                }
            }
            calendars
        } catch (e: SecurityException) {
            Log.e(TAG, "No calendar permission", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to query calendars", e)
            emptyList()
        }
    }

    /**
     * Insert a calendar event for a challenge.
     * Returns the event ID, or null if insertion failed.
     */
    fun insertChallengeEvent(
        context: Context,
        title: String,
        description: String,
        startMillis: Long,
        endMillis: Long,
        calendarId: Long? = null
    ): Long? {
        val resolvedCalendarId = calendarId ?: getDefaultWritableCalendarId(context)
        if (resolvedCalendarId == null) {
            Log.w(TAG, "No writable calendar found on device")
            return null
        }
        return try {
            val values = ContentValues().apply {
                put(CalendarContract.Events.CALENDAR_ID, resolvedCalendarId)
                put(CalendarContract.Events.TITLE, title)
                put(CalendarContract.Events.DESCRIPTION, description)
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                put(CalendarContract.Events.ALL_DAY, 1)
            }
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val id = uri?.lastPathSegment?.toLongOrNull()
            if (id != null) Log.d(TAG, "Challenge event inserted: $id")
            else Log.w(TAG, "Challenge event insert returned null URI")
            id
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception inserting challenge event", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert challenge event", e)
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
        durationMinutes: Int,
        calendarId: Long? = null
    ): Long? {
        val resolvedCalendarId = calendarId ?: getDefaultWritableCalendarId(context)
        if (resolvedCalendarId == null) {
            Log.w(TAG, "No writable calendar found on device")
            return null
        }
        return try {
            val endMillis = startMillis + (durationMinutes * 60 * 1000L)
            val values = ContentValues().apply {
                put(CalendarContract.Events.CALENDAR_ID, resolvedCalendarId)
                put(CalendarContract.Events.TITLE, title)
                put(CalendarContract.Events.DESCRIPTION, description)
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val id = uri?.lastPathSegment?.toLongOrNull()
            if (id != null) Log.d(TAG, "Workout event inserted: $id")
            else Log.w(TAG, "Workout event insert returned null URI")
            id
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception inserting workout event", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert workout event", e)
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
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete event $eventId", e)
            false
        }
    }

    /**
     * Find the first writable calendar on the device.
     * Queries for OWNER access first, then falls back to CONTRIBUTOR.
     * Avoids IS_PRIMARY which is unavailable on some OEMs (Samsung, etc.).
     */
    private fun getDefaultWritableCalendarId(context: Context): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE
        )

        return try {
            context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val idCol = cursor.getColumnIndex(CalendarContract.Calendars._ID)
                val accessCol = cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL)
                val accountTypeCol = cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE)

                if (idCol < 0 || accessCol < 0) {
                    Log.w(TAG, "Calendar columns not found")
                    return null
                }

                // First pass: prefer Google/owner calendar
                var bestId: Long? = null
                var bestAccess = -1

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val access = cursor.getInt(accessCol)
                    val accountType = if (accountTypeCol >= 0) cursor.getString(accountTypeCol) else ""

                    // Must be at least contributor level
                    if (access < CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR) continue

                    // Prefer Google account calendars, then highest access level
                    val isGoogle = accountType == "com.google"
                    val isOwner = access >= CalendarContract.Calendars.CAL_ACCESS_OWNER

                    if (isGoogle && isOwner) {
                        // Best possible — use immediately
                        return id
                    }

                    if (access > bestAccess || (isGoogle && bestAccess == access)) {
                        bestId = id
                        bestAccess = access
                    }
                }

                if (bestId == null) {
                    Log.w(TAG, "No writable calendar found. Total calendars scanned: ${cursor.count}")
                }
                bestId
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "No calendar permission", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to query calendars", e)
            null
        }
    }
}
