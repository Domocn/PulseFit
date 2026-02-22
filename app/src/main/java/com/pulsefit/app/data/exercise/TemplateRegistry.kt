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
        ),

        // ---- OTF_STYLE (12 new class formats) ----

        WorkoutTemplateData(
            id = "otf_2g_power", name = "2G Power",
            description = "Tread + Floor, no rowing — classic 2-group power format",
            durationMinutes = 60, type = "GUIDED",
            category = TemplateCategory.OTF_STYLE, difficulty = 3,
            phases = listOf(
                TemplatePhase("Warm-Up", 5, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300))),
                TemplatePhase("Tread Block 1", 10, "Push", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_push_pace", 360),
                    PhaseExercise("tread_all_out", 240)
                )),
                TemplatePhase("Floor Block 1", 10, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_chest_press", 180),
                    PhaseExercise("floor_push_ups", 120),
                    PhaseExercise("floor_trx_rows", 300)
                )),
                TemplatePhase("Tread Block 2", 12, "Push", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_base_pace", 360),
                    PhaseExercise("tread_push_pace", 240),
                    PhaseExercise("tread_all_out", 120)
                )),
                TemplatePhase("Floor Block 2", 12, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_squats", 240),
                    PhaseExercise("floor_lunges", 240),
                    PhaseExercise("floor_plank", 240)
                )),
                TemplatePhase("Tread Finisher", 6, "Peak", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 360))),
                TemplatePhase("Cool Down", 5, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300)))
            )
        ),

        WorkoutTemplateData(
            id = "otf_3g_endurance", name = "3G Endurance",
            description = "Equal Tread/Row/Floor blocks — sustained effort all stations",
            durationMinutes = 60, type = "GUIDED",
            category = TemplateCategory.OTF_STYLE, difficulty = 3,
            phases = listOf(
                TemplatePhase("Warm-Up", 5, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300))),
                TemplatePhase("Tread Block", 14, "Active", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_base_pace", 480),
                    PhaseExercise("tread_push_pace", 360)
                )),
                TemplatePhase("Row Block", 14, "Active", ExerciseStation.ROW, listOf(
                    PhaseExercise("row_steady", 480),
                    PhaseExercise("row_power", 360)
                )),
                TemplatePhase("Floor Block", 14, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_squats", 210),
                    PhaseExercise("floor_lunges", 210),
                    PhaseExercise("floor_push_ups", 210),
                    PhaseExercise("floor_plank", 210)
                )),
                TemplatePhase("Tread Finisher", 8, "Push", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_push_pace", 300),
                    PhaseExercise("tread_all_out", 180)
                )),
                TemplatePhase("Cool Down", 5, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300)))
            )
        ),

        WorkoutTemplateData(
            id = "otf_3g_strength", name = "3G Strength",
            description = "Heavy inclines + weights — strength-focused all stations",
            durationMinutes = 60, type = "GUIDED",
            category = TemplateCategory.OTF_STYLE, difficulty = 4,
            phases = listOf(
                TemplatePhase("Warm-Up", 5, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300))),
                TemplatePhase("Tread Inclines", 14, "Push", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_incline", 540),
                    PhaseExercise("tread_push_pace", 300)
                )),
                TemplatePhase("Row Power", 14, "Push", ExerciseStation.ROW, listOf(
                    PhaseExercise("row_power", 540),
                    PhaseExercise("row_all_out", 300)
                )),
                TemplatePhase("Floor Strength", 14, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_chest_press", 210),
                    PhaseExercise("floor_goblet_squats", 210),
                    PhaseExercise("floor_deadlifts", 210),
                    PhaseExercise("floor_trx_rows", 210)
                )),
                TemplatePhase("Tread Finisher", 8, "Peak", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_push_pace", 300),
                    PhaseExercise("tread_all_out", 180)
                )),
                TemplatePhase("Cool Down", 5, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300)))
            )
        ),

        WorkoutTemplateData(
            id = "otf_esp", name = "ESP",
            description = "Endurance-Strength-Power rotation across all stations",
            durationMinutes = 60, type = "GUIDED",
            category = TemplateCategory.OTF_STYLE, difficulty = 4,
            phases = listOf(
                TemplatePhase("Warm-Up", 5, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300))),
                TemplatePhase("Endurance", 14, "Active", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_base_pace", 540),
                    PhaseExercise("tread_push_pace", 300)
                )),
                TemplatePhase("Strength", 14, "Push", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_chest_press", 210),
                    PhaseExercise("floor_goblet_squats", 210),
                    PhaseExercise("floor_deadlifts", 210),
                    PhaseExercise("floor_lunges", 210)
                )),
                TemplatePhase("Power", 14, "Peak", null, listOf(
                    PhaseExercise("tread_all_out", 180),
                    PhaseExercise("row_all_out", 180),
                    PhaseExercise("floor_push_ups", 120),
                    PhaseExercise("floor_squats", 120),
                    PhaseExercise("tread_all_out", 120),
                    PhaseExercise("row_all_out", 120)
                )),
                TemplatePhase("Row Finisher", 8, "Push", ExerciseStation.ROW, listOf(
                    PhaseExercise("row_power", 300),
                    PhaseExercise("row_all_out", 180)
                )),
                TemplatePhase("Cool Down", 5, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300)))
            )
        ),

        WorkoutTemplateData(
            id = "otf_tornado", name = "Tornado",
            description = "Rapid 3-min station rotations — high intensity",
            durationMinutes = 45, type = "GUIDED",
            category = TemplateCategory.OTF_STYLE, difficulty = 5,
            phases = listOf(
                TemplatePhase("Warm-Up", 3, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 180))),
                TemplatePhase("Tread Round 1", 3, "Push", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 180))),
                TemplatePhase("Row Round 1", 3, "Push", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_all_out", 180))),
                TemplatePhase("Floor Round 1", 3, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_push_ups", 90),
                    PhaseExercise("floor_squats", 90)
                )),
                TemplatePhase("Tread Round 2", 3, "Push", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_push_pace", 180))),
                TemplatePhase("Row Round 2", 3, "Push", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_power", 180))),
                TemplatePhase("Floor Round 2", 3, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_lunges", 90),
                    PhaseExercise("floor_plank", 90)
                )),
                TemplatePhase("Tread Round 3", 3, "Peak", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 180))),
                TemplatePhase("Row Round 3", 3, "Peak", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_all_out", 180))),
                TemplatePhase("Floor Round 3", 3, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_chest_press", 90),
                    PhaseExercise("floor_goblet_squats", 90)
                )),
                TemplatePhase("Tread Finisher", 3, "Peak", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 180))),
                TemplatePhase("Row Finisher", 3, "Peak", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_all_out", 180))),
                TemplatePhase("Cool Down", 5, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300)))
            )
        ),

        WorkoutTemplateData(
            id = "otf_inferno", name = "Inferno",
            description = "Tread-heavy, max splat points — ultimate calorie burn",
            durationMinutes = 45, type = "GUIDED",
            category = TemplateCategory.OTF_STYLE, difficulty = 5,
            phases = listOf(
                TemplatePhase("Warm-Up", 3, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 180))),
                TemplatePhase("Push Climb", 8, "Push", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_push_pace", 300),
                    PhaseExercise("tread_incline", 180)
                )),
                TemplatePhase("All-Out Sprint", 4, "Peak", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 240))),
                TemplatePhase("Row Blast", 5, "Peak", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_all_out", 300))),
                TemplatePhase("Floor Burn", 5, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_burpees", 150),
                    PhaseExercise("floor_push_ups", 150)
                )),
                TemplatePhase("Tread Push 2", 8, "Push", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_push_pace", 240),
                    PhaseExercise("tread_all_out", 240)
                )),
                TemplatePhase("All-Out Finisher", 4, "Peak", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 240))),
                TemplatePhase("Cool Down", 8, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 480)))
            )
        ),

        WorkoutTemplateData(
            id = "otf_lift45_upper", name = "Lift 45 Upper",
            description = "Floor-focused upper body strength — 45 min",
            durationMinutes = 45, type = "GUIDED",
            category = TemplateCategory.OTF_STYLE, difficulty = 3,
            phases = listOf(
                TemplatePhase("Warm-Up", 5, "Warm-Up", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_steady", 300))),
                TemplatePhase("Chest & Triceps", 10, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_chest_press", 300),
                    PhaseExercise("floor_push_ups", 300)
                )),
                TemplatePhase("Back & Biceps", 10, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_trx_rows", 300),
                    PhaseExercise("floor_bicep_curls", 300)
                )),
                TemplatePhase("Shoulders & Core", 10, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_lateral_raises", 300),
                    PhaseExercise("floor_plank", 300)
                )),
                TemplatePhase("Upper Finisher", 5, "Push", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_push_ups", 150),
                    PhaseExercise("floor_chest_press", 150)
                )),
                TemplatePhase("Cool Down", 5, "Rest", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_steady", 300)))
            )
        ),

        WorkoutTemplateData(
            id = "otf_lift45_lower", name = "Lift 45 Lower",
            description = "Floor-focused lower body strength — 45 min",
            durationMinutes = 45, type = "GUIDED",
            category = TemplateCategory.OTF_STYLE, difficulty = 3,
            phases = listOf(
                TemplatePhase("Warm-Up", 5, "Warm-Up", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_steady", 300))),
                TemplatePhase("Squats & Lunges", 10, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_goblet_squats", 300),
                    PhaseExercise("floor_lunges", 300)
                )),
                TemplatePhase("Deadlifts & Glutes", 10, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_deadlifts", 300),
                    PhaseExercise("floor_squats", 300)
                )),
                TemplatePhase("Core & Stability", 10, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_plank", 300),
                    PhaseExercise("floor_lateral_raises", 300)
                )),
                TemplatePhase("Lower Finisher", 5, "Push", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_goblet_squats", 150),
                    PhaseExercise("floor_lunges", 150)
                )),
                TemplatePhase("Cool Down", 5, "Rest", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_steady", 300)))
            )
        ),

        WorkoutTemplateData(
            id = "otf_23_burn", name = "23-Min Burn",
            description = "Short high-intensity blast — max effort in minimal time",
            durationMinutes = 23, type = "GUIDED",
            category = TemplateCategory.OTF_STYLE, difficulty = 4,
            phases = listOf(
                TemplatePhase("Warm-Up", 2, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 120))),
                TemplatePhase("Tread Push", 5, "Push", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_push_pace", 180),
                    PhaseExercise("tread_all_out", 120)
                )),
                TemplatePhase("Row Blast", 4, "Peak", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_all_out", 240))),
                TemplatePhase("Floor Burn", 5, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_burpees", 100),
                    PhaseExercise("floor_push_ups", 100),
                    PhaseExercise("floor_squats", 100)
                )),
                TemplatePhase("Tread Finisher", 4, "Peak", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 240))),
                TemplatePhase("Cool Down", 3, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 180)))
            )
        ),

        WorkoutTemplateData(
            id = "otf_90_marathon", name = "90-Min Marathon",
            description = "Extended all-station deep workout — the ultimate session",
            durationMinutes = 90, type = "GUIDED",
            category = TemplateCategory.OTF_STYLE, difficulty = 4,
            phases = listOf(
                TemplatePhase("Warm-Up", 5, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300))),
                TemplatePhase("Tread Endurance", 20, "Active", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_base_pace", 600),
                    PhaseExercise("tread_push_pace", 600)
                )),
                TemplatePhase("Row Endurance", 15, "Active", ExerciseStation.ROW, listOf(
                    PhaseExercise("row_steady", 600),
                    PhaseExercise("row_power", 300)
                )),
                TemplatePhase("Floor Block 1", 12, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_chest_press", 180),
                    PhaseExercise("floor_goblet_squats", 180),
                    PhaseExercise("floor_deadlifts", 180),
                    PhaseExercise("floor_plank", 180)
                )),
                TemplatePhase("Tread Push", 10, "Push", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_push_pace", 360),
                    PhaseExercise("tread_all_out", 240)
                )),
                TemplatePhase("Row Power", 8, "Push", ExerciseStation.ROW, listOf(
                    PhaseExercise("row_power", 240),
                    PhaseExercise("row_all_out", 240)
                )),
                TemplatePhase("Floor Block 2", 10, "Active", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_lunges", 200),
                    PhaseExercise("floor_push_ups", 200),
                    PhaseExercise("floor_trx_rows", 200)
                )),
                TemplatePhase("All-Out Finisher", 5, "Peak", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_all_out", 300))),
                TemplatePhase("Cool Down", 5, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300)))
            )
        ),

        WorkoutTemplateData(
            id = "otf_partner", name = "Partner Throwdown",
            description = "Alternating station buddy format — push each other",
            durationMinutes = 60, type = "GUIDED",
            category = TemplateCategory.OTF_STYLE, difficulty = 4,
            phases = listOf(
                TemplatePhase("Warm-Up", 5, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300))),
                TemplatePhase("Partner A: Tread / B: Row", 8, "Push", null, listOf(
                    PhaseExercise("tread_push_pace", 480, notes = "Partner A on tread"),
                    PhaseExercise("row_power", 480, notes = "Partner B on rower")
                )),
                TemplatePhase("Switch: A: Row / B: Tread", 8, "Push", null, listOf(
                    PhaseExercise("row_power", 480, notes = "Partner A on rower"),
                    PhaseExercise("tread_push_pace", 480, notes = "Partner B on tread")
                )),
                TemplatePhase("Partner A: Floor / B: Tread", 8, "Active", null, listOf(
                    PhaseExercise("floor_push_ups", 120),
                    PhaseExercise("floor_squats", 120),
                    PhaseExercise("floor_plank", 240),
                    PhaseExercise("tread_all_out", 480, notes = "Partner B all-out")
                )),
                TemplatePhase("Switch: A: Tread / B: Floor", 8, "Active", null, listOf(
                    PhaseExercise("tread_all_out", 480, notes = "Partner A all-out"),
                    PhaseExercise("floor_push_ups", 120),
                    PhaseExercise("floor_squats", 120),
                    PhaseExercise("floor_plank", 240)
                )),
                TemplatePhase("Both: Row All-Out", 8, "Peak", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_all_out", 480))),
                TemplatePhase("Both: Tread Finisher", 10, "Peak", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_push_pace", 360),
                    PhaseExercise("tread_all_out", 240)
                )),
                TemplatePhase("Cool Down", 5, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300)))
            )
        ),

        WorkoutTemplateData(
            id = "otf_benchmark", name = "Benchmark Day",
            description = "Timed challenge benchmarks — test your limits",
            durationMinutes = 60, type = "GUIDED",
            category = TemplateCategory.OTF_STYLE, difficulty = 5,
            phases = listOf(
                TemplatePhase("Warm-Up", 5, "Warm-Up", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300))),
                TemplatePhase("Tread Benchmark", 12, "Peak", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_all_out", 720, notes = "12-min distance benchmark")
                )),
                TemplatePhase("Recovery", 3, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 180))),
                TemplatePhase("Row Benchmark", 10, "Peak", ExerciseStation.ROW, listOf(
                    PhaseExercise("row_all_out", 600, notes = "Row for max distance")
                )),
                TemplatePhase("Recovery", 3, "Rest", ExerciseStation.ROW,
                    listOf(PhaseExercise("row_steady", 180))),
                TemplatePhase("Floor Benchmark", 12, "Push", ExerciseStation.FLOOR, listOf(
                    PhaseExercise("floor_push_ups", 180, notes = "Max reps in 3 min"),
                    PhaseExercise("floor_squats", 180, notes = "Max reps in 3 min"),
                    PhaseExercise("floor_burpees", 180, notes = "Max reps in 3 min"),
                    PhaseExercise("floor_plank", 180, notes = "Max hold time")
                )),
                TemplatePhase("Tread Finisher", 10, "Peak", ExerciseStation.TREAD, listOf(
                    PhaseExercise("tread_push_pace", 360),
                    PhaseExercise("tread_all_out", 240)
                )),
                TemplatePhase("Cool Down", 5, "Rest", ExerciseStation.TREAD,
                    listOf(PhaseExercise("tread_power_walk", 300)))
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
