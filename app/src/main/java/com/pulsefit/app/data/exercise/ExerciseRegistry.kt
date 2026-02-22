package com.pulsefit.app.data.exercise

import com.pulsefit.app.data.model.Exercise
import com.pulsefit.app.data.model.ExerciseStation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRegistry @Inject constructor() {

    private val exercises: Map<String, Exercise> = listOf(
        // Tread (5)
        Exercise("tread_base_pace", "Base Pace", "Steady jog at conversational pace", ExerciseStation.TREAD, listOf("Quads", "Calves", "Glutes"), null, 300),
        Exercise("tread_push_pace", "Push Pace", "Faster pace, challenging but sustainable", ExerciseStation.TREAD, listOf("Quads", "Hamstrings", "Glutes"), null, 180),
        Exercise("tread_all_out", "All-Out Sprint", "Maximum effort sprint", ExerciseStation.TREAD, listOf("Full Lower Body", "Core"), null, 60),
        Exercise("tread_power_walk", "Power Walk", "Brisk walk with purpose", ExerciseStation.TREAD, listOf("Glutes", "Calves"), null, 180),
        Exercise("tread_incline", "Incline Walk", "Walk at elevated incline for glute activation", ExerciseStation.TREAD, listOf("Glutes", "Hamstrings", "Calves"), null, 300),

        // Row (3)
        Exercise("row_steady", "Steady Row", "Consistent rowing at moderate pace", ExerciseStation.ROW, listOf("Back", "Legs", "Arms"), null, 300),
        Exercise("row_power", "Power Row", "Strong pulls with explosive leg drive", ExerciseStation.ROW, listOf("Back", "Legs", "Core"), null, 180),
        Exercise("row_all_out", "All-Out Row", "Maximum effort rowing sprint", ExerciseStation.ROW, listOf("Full Body"), null, 60),

        // Floor (12)
        Exercise("floor_squats", "Squats", "Bodyweight or weighted squats", ExerciseStation.FLOOR, listOf("Quads", "Glutes"), null, 60),
        Exercise("floor_lunges", "Lunges", "Alternating forward lunges", ExerciseStation.FLOOR, listOf("Quads", "Glutes", "Hamstrings"), null, 60),
        Exercise("floor_deadlifts", "Deadlifts", "Hip hinge with weights", ExerciseStation.FLOOR, listOf("Hamstrings", "Glutes", "Lower Back"), null, 60),
        Exercise("floor_chest_press", "Chest Press", "Dumbbell chest press on bench", ExerciseStation.FLOOR, listOf("Chest", "Triceps"), null, 60),
        Exercise("floor_shoulder_press", "Shoulder Press", "Overhead dumbbell press", ExerciseStation.FLOOR, listOf("Shoulders", "Triceps"), null, 60),
        Exercise("floor_bicep_curls", "Bicep Curls", "Dumbbell curls", ExerciseStation.FLOOR, listOf("Biceps"), null, 45),
        Exercise("floor_tricep_ext", "Tricep Extensions", "Overhead tricep extensions", ExerciseStation.FLOOR, listOf("Triceps"), null, 45),
        Exercise("floor_push_ups", "Push-Ups", "Standard push-ups", ExerciseStation.FLOOR, listOf("Chest", "Triceps", "Core"), null, 45),
        Exercise("floor_plank", "Plank", "Hold plank position", ExerciseStation.FLOOR, listOf("Core", "Shoulders"), null, 45),
        Exercise("floor_trx_rows", "TRX Rows", "Suspended body rows", ExerciseStation.FLOOR, listOf("Back", "Biceps"), null, 45),
        Exercise("floor_bench_hops", "Bench Hop-Overs", "Lateral hops over the bench", ExerciseStation.FLOOR, listOf("Full Body", "Core"), null, 30),
        Exercise("floor_pop_squats", "Pop Squats", "Explosive squat jumps", ExerciseStation.FLOOR, listOf("Quads", "Glutes", "Calves"), null, 30)
    ).associateBy { it.id }

    fun getAll(): List<Exercise> = exercises.values.toList()

    fun getById(id: String): Exercise? = exercises[id]

    fun getByStation(station: ExerciseStation): List<Exercise> =
        exercises.values.filter { it.station == station }
}
