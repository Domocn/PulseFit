package com.pulsefit.app.data.exercise

import com.pulsefit.app.data.model.ExerciseStation
import com.pulsefit.app.data.model.PhaseExercise
import com.pulsefit.app.data.model.TemplateCategory
import com.pulsefit.app.data.model.TemplatePhase
import com.pulsefit.app.data.model.WorkoutTemplateData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateRegistry @Inject constructor() {

    private val templates: List<WorkoutTemplateData> = listOf(
        // ---- STANDARD (5 existing, expanded with exercises) ----

        WorkoutTemplateData(
            id = "free_run", name = "Free Run",
            description = "Open-ended workout, end when ready",
            durationMinutes = 0, type = "FREE",
            category = TemplateCategory.STANDARD, difficulty = 1,
            phases = emptyList()
        ),

        WorkoutTemplateData(
            id = "quick_15", name = "Quick 15",
            description = "15-minute intensity burst across tread and row",
            durationMinutes = 15, type = "GUIDED",
            category = TemplateCategory.STANDARD, difficulty = 2,
            phases = listOf(
                TemplatePhase("Warm-Up", 3, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 180))),
                TemplatePhase("Main Set", 10, "Active", null, listOf(
                    PhaseExercise("tread_push_pace", 300, notes = "Push pace on tread"),
                    PhaseExercise("row_power", 300, notes = "Power row")
                )),
                TemplatePhase("Cool Down", 2, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 120)))
            )
        ),

        WorkoutTemplateData(
            id = "steady_state", name = "Steady State",
            description = "30 minutes of sustained effort on tread and row",
            durationMinutes = 30, type = "GUIDED",
            category = TemplateCategory.STANDARD, difficulty = 2,
            phases = listOf(
                TemplatePhase("Warm-Up", 5, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300))),
                TemplatePhase("Tread Active", 10, "Active", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_base_pace", 600))),
                TemplatePhase("Row Active", 10, "Active", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_steady", 600))),
                TemplatePhase("Cool Down", 5, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300)))
            )
        ),

        WorkoutTemplateData(
            id = "hiit_intervals", name = "HIIT Intervals",
            description = "Alternating Push/Active zones with all-out efforts",
            durationMinutes = 20, type = "GUIDED",
            category = TemplateCategory.STANDARD, difficulty = 4,
            phases = listOf(
                TemplatePhase("Warm-Up", 3, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 180))),
                TemplatePhase("Push 1", 2, "Push", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 120))),
                TemplatePhase("Recovery 1", 2, "Active", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_base_pace", 120))),
                TemplatePhase("Push 2", 2, "Push", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 120))),
                TemplatePhase("Recovery 2", 2, "Active", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_base_pace", 120))),
                TemplatePhase("Push 3", 2, "Push", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 120))),
                TemplatePhase("Recovery 3", 2, "Active", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_base_pace", 120))),
                TemplatePhase("Row Push", 3, "Push", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_all_out", 180))),
                TemplatePhase("Cool Down", 2, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 120)))
            )
        ),

        WorkoutTemplateData(
            id = "endurance", name = "Endurance",
            description = "45-minute sustained effort across all stations",
            durationMinutes = 45, type = "GUIDED",
            category = TemplateCategory.STANDARD, difficulty = 3,
            phases = listOf(
                TemplatePhase("Warm-Up", 5, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300))),
                TemplatePhase("Tread Block", 15, "Active", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_base_pace", 480),
                    PhaseExercise("tread_push_pace", 420)
                )),
                TemplatePhase("Row Block", 10, "Active", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_steady", 600))),
                TemplatePhase("Floor Block", 10, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_squats", 180),
                    PhaseExercise("floor_lunges", 180),
                    PhaseExercise("floor_plank", 240)
                )),
                TemplatePhase("Cool Down", 5, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300)))
            )
        ),

        // ---- BEGINNER (3 new) ----

        WorkoutTemplateData(
            id = "gentle_start", name = "Gentle Start",
            description = "Easy 10-minute intro to get moving",
            durationMinutes = 10, type = "GUIDED",
            category = TemplateCategory.BEGINNER, difficulty = 1,
            phases = listOf(
                TemplatePhase("Walk", 3, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 180))),
                TemplatePhase("Light Jog", 5, "Active", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_base_pace", 300))),
                TemplatePhase("Cool Down", 2, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 120)))
            )
        ),

        WorkoutTemplateData(
            id = "walk_and_jog", name = "Walk & Jog",
            description = "20-minute mix of walking, jogging, rowing, and floor",
            durationMinutes = 20, type = "GUIDED",
            category = TemplateCategory.BEGINNER, difficulty = 1,
            phases = listOf(
                TemplatePhase("Walk", 4, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 240))),
                TemplatePhase("Light Jog", 5, "Active", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_base_pace", 300))),
                TemplatePhase("Row", 4, "Active", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_steady", 240))),
                TemplatePhase("Floor", 4, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_squats", 120),
                    PhaseExercise("floor_lunges", 120)
                )),
                TemplatePhase("Cool Down", 3, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 180)))
            )
        ),

        WorkoutTemplateData(
            id = "easy_active", name = "Easy Active",
            description = "15-minute gentle tread and row session",
            durationMinutes = 15, type = "GUIDED",
            category = TemplateCategory.BEGINNER, difficulty = 1,
            phases = listOf(
                TemplatePhase("Warm-Up", 3, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 180))),
                TemplatePhase("Tread", 4, "Active", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_base_pace", 240))),
                TemplatePhase("Row", 4, "Active", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_steady", 240))),
                TemplatePhase("Cool Down", 4, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 240)))
            )
        ),

        // ---- ADVANCED (3 new) ----

        WorkoutTemplateData(
            id = "tabata", name = "Tabata",
            description = "20-minute Tabata-style intervals on tread and row",
            durationMinutes = 20, type = "GUIDED",
            category = TemplateCategory.ADVANCED, difficulty = 5,
            phases = listOf(
                TemplatePhase("Warm-Up", 3, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 180))),
                TemplatePhase("Tread Tabata", 4, "Peak", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_all_out", 20, notes = "All-out 20s"),
                    PhaseExercise("tread_power_walk", 10, notes = "Rest 10s")
                )),
                TemplatePhase("Transition", 1, "Rest", null, emptyList()),
                TemplatePhase("Row Tabata", 4, "Peak", ExerciseStation.ROW, listOf(
                    PhaseExercise("row_all_out", 20, notes = "All-out 20s"),
                    PhaseExercise("row_steady", 10, notes = "Rest 10s")
                )),
                TemplatePhase("Cool Down", 4, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 240)))
            )
        ),

        WorkoutTemplateData(
            id = "hill_climb", name = "Hill Climb",
            description = "30-minute progressive intensity climb",
            durationMinutes = 30, type = "GUIDED",
            category = TemplateCategory.ADVANCED, difficulty = 4,
            phases = listOf(
                TemplatePhase("Warm-Up", 5, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300))),
                TemplatePhase("Incline", 5, "Active", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_incline", 300))),
                TemplatePhase("Push Pace", 5, "Push", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_push_pace", 300))),
                TemplatePhase("All-Out", 5, "Peak", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 300))),
                TemplatePhase("Power Row", 5, "Push", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_power", 300))),
                TemplatePhase("Cool Down", 5, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300)))
            )
        ),

        WorkoutTemplateData(
            id = "sprint_intervals", name = "Sprint Intervals",
            description = "25-minute sprint and recovery cycles",
            durationMinutes = 25, type = "GUIDED",
            category = TemplateCategory.ADVANCED, difficulty = 4,
            phases = listOf(
                TemplatePhase("Warm-Up", 4, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 240))),
                TemplatePhase("Sprint 1", 1, "Peak", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 60))),
                TemplatePhase("Recovery 1", 2, "Active", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_base_pace", 120))),
                TemplatePhase("Sprint 2", 1, "Peak", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 60))),
                TemplatePhase("Recovery 2", 2, "Active", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_base_pace", 120))),
                TemplatePhase("Sprint 3", 1, "Peak", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 60))),
                TemplatePhase("Recovery 3", 2, "Active", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_base_pace", 120))),
                TemplatePhase("Sprint 4", 1, "Peak", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 60))),
                TemplatePhase("Recovery 4", 2, "Active", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_base_pace", 120))),
                TemplatePhase("Sprint 5", 1, "Peak", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 60))),
                TemplatePhase("Cool Down", 6, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 360)))
            )
        ),

        // ---- SPECIALTY (3 new) ----

        WorkoutTemplateData(
            id = "recovery", name = "Recovery",
            description = "15-minute gentle recovery session",
            durationMinutes = 15, type = "GUIDED",
            category = TemplateCategory.SPECIALTY, difficulty = 1,
            phases = listOf(
                TemplatePhase("Walk", 5, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300))),
                TemplatePhase("Gentle Row", 5, "Warm-Up", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_steady", 300))),
                TemplatePhase("Floor Stretch", 5, "Rest", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_plank", 150),
                    PhaseExercise("floor_squats", 150, notes = "Slow and controlled")
                ))
            )
        ),

        WorkoutTemplateData(
            id = "stress_relief", name = "Stress Relief",
            description = "20-minute calm, steady movement for stress relief",
            durationMinutes = 20, type = "GUIDED",
            category = TemplateCategory.SPECIALTY, difficulty = 1,
            phases = listOf(
                TemplatePhase("Walk", 5, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300))),
                TemplatePhase("Steady Row", 5, "Warm-Up", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_steady", 300))),
                TemplatePhase("Floor", 5, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_squats", 150, notes = "Slow tempo"),
                    PhaseExercise("floor_lunges", 150, notes = "Slow tempo")
                )),
                TemplatePhase("Cool Down", 5, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300)))
            )
        ),

        WorkoutTemplateData(
            id = "morning_energizer", name = "Morning Energizer",
            description = "10-minute wake-up burst to start the day",
            durationMinutes = 10, type = "GUIDED",
            category = TemplateCategory.SPECIALTY, difficulty = 2,
            phases = listOf(
                TemplatePhase("Warm-Up", 2, "Warm-Up", ExerciseStation.FLOOR,
                    listOf(PhaseExercise("floor_squats", 120, notes = "Light warm-up"))),
                TemplatePhase("Floor Burst", 2, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_push_ups", 60),
                    PhaseExercise("floor_squats", 60)
                )),
                TemplatePhase("Push Pace", 3, "Push", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_push_pace", 180))),
                TemplatePhase("Power Row", 3, "Push", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_power", 180)))
            )
        )
    )

    fun getAll(): List<WorkoutTemplateData> = templates

    fun getById(id: String): WorkoutTemplateData? = templates.find { it.id == id }

    fun getByCategory(category: TemplateCategory): List<WorkoutTemplateData> =
        templates.filter { it.category == category }

    fun getStationsUsed(template: WorkoutTemplateData): Set<ExerciseStation> =
        template.phases.mapNotNull { it.station }.toSet()

    fun getExerciseCount(template: WorkoutTemplateData): Int =
        template.phases.flatMap { it.exercises }.map { it.exerciseId }.distinct().size
}
