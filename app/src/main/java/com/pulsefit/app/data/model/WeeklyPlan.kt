package com.pulsefit.app.data.model

/**
 * A generated weekly workout plan.
 */
data class WeeklyPlan(
    val id: String = "",
    val weekStartDate: Long = 0L,
    val days: List<PlannedDay> = emptyList(),
    val generatedAt: Long = System.currentTimeMillis(),
    val environment: WorkoutEnvironment = WorkoutEnvironment.GYM
)

/**
 * One day in the weekly plan.
 */
data class PlannedDay(
    val dayOfWeek: DayOfWeek,
    val type: PlannedDayType,
    val templateId: String? = null,
    val templateName: String? = null,
    val durationMinutes: Int = 0,
    val focus: String = "",
    val completed: Boolean = false,
    val calendarEventId: Long? = null
)

enum class PlannedDayType(val label: String) {
    WORKOUT("Workout"),
    REST("Rest Day"),
    ACTIVE_RECOVERY("Active Recovery"),
    CHALLENGE("Challenge")
}
