package com.pulsefit.app.data.exercise

import com.pulsefit.app.data.model.DayOfWeek
import com.pulsefit.app.data.model.Equipment
import com.pulsefit.app.data.model.EquipmentProfile
import com.pulsefit.app.data.model.ExerciseStation
import com.pulsefit.app.data.model.PlannedDay
import com.pulsefit.app.data.model.PlannedDayType
import com.pulsefit.app.data.model.WeeklyPlan
import com.pulsefit.app.data.model.WorkoutEnvironment
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates weekly workout plans based on user's equipment, environment,
 * and preferred schedule. Ensures rest days, variety, and progressive focus.
 */
@Singleton
class WeeklyPlanGenerator @Inject constructor(
    private val templateRegistry: TemplateRegistry
) {

    fun generate(
        equipmentProfile: EquipmentProfile,
        weekStartDate: Long = System.currentTimeMillis()
    ): WeeklyPlan {
        val workoutDays = equipmentProfile.preferredWorkoutDays
        val workoutsPerWeek = equipmentProfile.workoutsPerWeek
            .coerceIn(1, workoutDays.size)

        val hasTreadmill = equipmentProfile.availableEquipment.contains(Equipment.TREADMILL)
        val hasRower = equipmentProfile.availableEquipment.contains(Equipment.ROWER)
        val hasDumbbells = equipmentProfile.availableEquipment.contains(Equipment.DUMBBELLS)
        val hasResistanceBands = equipmentProfile.availableEquipment.contains(Equipment.RESISTANCE_BANDS)

        // Build focus rotation based on available equipment
        val focusRotation = buildFocusRotation(equipmentProfile)

        // Assign workouts to preferred days, rest to others
        val selectedWorkoutDays = workoutDays.toList()
            .take(workoutsPerWeek)

        val days = DayOfWeek.entries.mapIndexed { index, day ->
            if (day in selectedWorkoutDays) {
                val focusIndex = selectedWorkoutDays.indexOf(day)
                val focus = focusRotation[focusIndex % focusRotation.size]
                val template = pickTemplateForFocus(
                    focus = focus,
                    environment = equipmentProfile.environment,
                    hasTreadmill = hasTreadmill,
                    hasRower = hasRower,
                    preferredDuration = equipmentProfile.preferredDurationMinutes
                )
                PlannedDay(
                    dayOfWeek = day,
                    type = PlannedDayType.WORKOUT,
                    templateId = template?.first,
                    templateName = template?.second,
                    durationMinutes = template?.third ?: equipmentProfile.preferredDurationMinutes,
                    focus = focus
                )
            } else {
                // Check if this is between two workout days → active recovery
                val prevDay = DayOfWeek.entries.getOrNull(index - 1)
                val nextDay = DayOfWeek.entries.getOrNull(index + 1)
                val isBetweenWorkouts = prevDay in selectedWorkoutDays && nextDay in selectedWorkoutDays

                PlannedDay(
                    dayOfWeek = day,
                    type = if (isBetweenWorkouts) PlannedDayType.ACTIVE_RECOVERY else PlannedDayType.REST,
                    focus = if (isBetweenWorkouts) "Light movement, stretching" else "Rest and recover"
                )
            }
        }

        return WeeklyPlan(
            id = "plan_${weekStartDate}",
            weekStartDate = weekStartDate,
            days = days,
            environment = equipmentProfile.environment
        )
    }

    private fun buildFocusRotation(profile: EquipmentProfile): List<String> {
        val hasTreadmill = profile.availableEquipment.contains(Equipment.TREADMILL)
        val hasRower = profile.availableEquipment.contains(Equipment.ROWER)
        val hasDumbbells = profile.availableEquipment.contains(Equipment.DUMBBELLS)
            || profile.availableEquipment.contains(Equipment.KETTLEBELL)
        val hasBands = profile.availableEquipment.contains(Equipment.RESISTANCE_BANDS)
            || profile.availableEquipment.contains(Equipment.TRX)

        return when (profile.environment) {
            WorkoutEnvironment.GYM -> when {
                hasTreadmill && hasRower -> listOf(
                    "Endurance (Tread)",
                    "Strength (Floor)",
                    "Power (Row + Tread)",
                    "HIIT (Full Rotation)",
                    "Endurance (Row)"
                )
                hasTreadmill -> listOf(
                    "Endurance (Tread)",
                    "Strength (Floor)",
                    "HIIT (Tread + Floor)",
                    "Endurance (Tread)"
                )
                else -> listOf(
                    "Strength (Floor)",
                    "HIIT (Bodyweight)",
                    "Endurance (Cardio)",
                    "Strength (Floor)"
                )
            }
            WorkoutEnvironment.HOME -> when {
                hasDumbbells && hasBands -> listOf(
                    "Upper Body (Dumbbells)",
                    "Lower Body (Bands + Bodyweight)",
                    "Full Body HIIT",
                    "Core + Mobility"
                )
                hasDumbbells -> listOf(
                    "Upper Body (Dumbbells)",
                    "Lower Body (Bodyweight)",
                    "Full Body Circuit",
                    "Core + Mobility"
                )
                else -> listOf(
                    "Bodyweight HIIT",
                    "Core + Flexibility",
                    "Bodyweight Strength",
                    "Cardio Intervals"
                )
            }
            WorkoutEnvironment.OUTDOOR -> listOf(
                "Run / Walk",
                "Bodyweight Park Workout",
                "Hill Intervals",
                "Distance Run"
            )
            WorkoutEnvironment.HOTEL -> listOf(
                "Bodyweight HIIT (No Equipment)",
                "Core + Mobility",
                "Bodyweight Strength",
                "Cardio in Room"
            )
        }
    }

    private fun pickTemplateForFocus(
        focus: String,
        environment: WorkoutEnvironment,
        hasTreadmill: Boolean,
        hasRower: Boolean,
        preferredDuration: Int
    ): Triple<String, String, Int>? {
        // Match focus to existing templates where possible
        val templates = templateRegistry.getAll()

        val match = when {
            focus.contains("HIIT", ignoreCase = true) ->
                templates.find { it.id == "hiit_intervals" }
            focus.contains("Endurance", ignoreCase = true) && focus.contains("Tread") ->
                templates.find { it.id == "steady_state" || it.id == "endurance" }
            focus.contains("Endurance", ignoreCase = true) && focus.contains("Row") ->
                templates.find { it.id == "steady_state" }
            focus.contains("Power", ignoreCase = true) ->
                templates.find { it.id == "sprint_intervals" || it.id == "hill_climb" }
            focus.contains("Strength", ignoreCase = true) ->
                templates.find { it.id == "endurance" } // Has floor block
            focus.contains("Morning", ignoreCase = true) ->
                templates.find { it.id == "morning_energizer" }
            focus.contains("Recovery", ignoreCase = true) || focus.contains("Mobility", ignoreCase = true) ->
                templates.find { it.id == "recovery" || it.id == "stress_relief" }
            focus.contains("Bodyweight", ignoreCase = true) ->
                templates.find { it.id == "morning_energizer" }
            // Duration-based fallback
            preferredDuration <= 15 ->
                templates.find { it.id == "quick_15" }
            preferredDuration <= 20 ->
                templates.find { it.id == "hiit_intervals" || it.id == "walk_and_jog" }
            preferredDuration <= 30 ->
                templates.find { it.id == "steady_state" }
            else ->
                templates.find { it.id == "endurance" }
        }

        return match?.let { Triple(it.id, it.name, it.durationMinutes) }
    }
}
