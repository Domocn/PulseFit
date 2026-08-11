package com.pulsefit.app.data.exercise

import com.pulsefit.app.data.model.Exercise
import com.pulsefit.app.data.model.ExerciseStation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRegistry @Inject constructor() {

    private val exercises: Map<String, Exercise> = listOf(
        // Tread (5)
        Exercise("tread_base_pace", "Base Pace", "Steady jog at conversational pace", ExerciseStation.TREAD, listOf("Quads", "Calves", "Glutes"), null, 300,
            formTips = listOf("Land midfoot, not on heels", "Keep shoulders relaxed and down", "Arms at 90 degrees, swing forward not across")),
        Exercise("tread_push_pace", "Push Pace", "Faster pace, challenging but sustainable", ExerciseStation.TREAD, listOf("Quads", "Hamstrings", "Glutes"), null, 180,
            formTips = listOf("Lean slightly forward from ankles", "Quick turnover, shorter strides", "Breathe rhythmically: in 2 steps, out 2 steps")),
        Exercise("tread_all_out", "All-Out Sprint", "Maximum effort sprint", ExerciseStation.TREAD, listOf("Full Lower Body", "Core"), null, 60,
            formTips = listOf("Drive knees high", "Pump arms aggressively", "Stay tall, don't hunch over")),
        Exercise("tread_power_walk", "Power Walk", "Brisk walk with purpose", ExerciseStation.TREAD, listOf("Glutes", "Calves"), null, 180,
            formTips = listOf("Heel strike, roll through to toes", "Engage core, stand tall", "Pump arms to increase intensity")),
        Exercise("tread_incline", "Incline Walk", "Walk at elevated incline for glute activation", ExerciseStation.TREAD, listOf("Glutes", "Hamstrings", "Calves"), null, 300,
            formTips = listOf("Don't hold the handrails", "Shorten stride on steep inclines", "Push through heels to engage glutes")),

        // Row (3)
        Exercise("row_steady", "Steady Row", "Consistent rowing at moderate pace", ExerciseStation.ROW, listOf("Back", "Legs", "Arms"), null, 300,
            formTips = listOf("Drive with legs first, then lean back, then pull arms", "Keep core engaged throughout", "Return in reverse: arms, body, legs")),
        Exercise("row_power", "Power Row", "Strong pulls with explosive leg drive", ExerciseStation.ROW, listOf("Back", "Legs", "Core"), null, 180,
            formTips = listOf("Explosive leg drive off the footplate", "Squeeze shoulder blades at the finish", "Keep wrists flat, not curled")),
        Exercise("row_all_out", "All-Out Row", "Maximum effort rowing sprint", ExerciseStation.ROW, listOf("Full Body"), null, 60,
            formTips = listOf("Max power on leg drive", "Quick hands on the recovery", "Maintain form even at max effort")),

        // Floor (12)
        Exercise("floor_squats", "Squats", "Bodyweight or weighted squats", ExerciseStation.FLOOR, listOf("Quads", "Glutes"), null, 60,
            formTips = listOf("Knees track over toes, don't cave in", "Sit back like sitting in a chair", "Keep chest up, core braced")),
        Exercise("floor_lunges", "Lunges", "Alternating forward lunges", ExerciseStation.FLOOR, listOf("Quads", "Glutes", "Hamstrings"), null, 60,
            formTips = listOf("Front knee stays over ankle, not past toes", "Step far enough forward for 90-degree angles", "Keep torso upright")),
        Exercise("floor_deadlifts", "Deadlifts", "Hip hinge with weights", ExerciseStation.FLOOR, listOf("Hamstrings", "Glutes", "Lower Back"), null, 60,
            formTips = listOf("Hinge at hips, not waist", "Keep weights close to your body", "Flat back throughout, no rounding")),
        Exercise("floor_chest_press", "Chest Press", "Dumbbell chest press on bench", ExerciseStation.FLOOR, listOf("Chest", "Triceps"), null, 60,
            formTips = listOf("Feet flat on floor, slight arch in lower back", "Lower weights to chest level, elbows at 45 degrees", "Press up and slightly inward")),
        Exercise("floor_shoulder_press", "Shoulder Press", "Overhead dumbbell press", ExerciseStation.FLOOR, listOf("Shoulders", "Triceps"), null, 60,
            formTips = listOf("Start at ear level, press straight up", "Don't arch lower back", "Core tight throughout the movement")),
        Exercise("floor_bicep_curls", "Bicep Curls", "Dumbbell curls", ExerciseStation.FLOOR, listOf("Biceps"), null, 45,
            formTips = listOf("Keep elbows pinned to your sides", "Control the lowering phase", "Full range of motion, no swinging")),
        Exercise("floor_tricep_ext", "Tricep Extensions", "Overhead tricep extensions", ExerciseStation.FLOOR, listOf("Triceps"), null, 45,
            formTips = listOf("Keep elbows close to ears", "Lower weight behind head slowly", "Extend fully at the top")),
        Exercise("floor_push_ups", "Push-Ups", "Standard push-ups", ExerciseStation.FLOOR, listOf("Chest", "Triceps", "Core"), null, 45,
            formTips = listOf("Hands slightly wider than shoulders", "Body in a straight line from head to heels", "Lower chest to floor, not just head")),
        Exercise("floor_plank", "Plank", "Hold plank position", ExerciseStation.FLOOR, listOf("Core", "Shoulders"), null, 45,
            formTips = listOf("Elbows under shoulders", "Squeeze glutes and brace core", "Don't let hips sag or pike up")),
        Exercise("floor_trx_rows", "TRX Rows", "Suspended body rows", ExerciseStation.FLOOR, listOf("Back", "Biceps"), null, 45,
            formTips = listOf("Keep body rigid like a plank", "Pull shoulder blades together", "Walk feet closer to increase difficulty")),
        Exercise("floor_bench_hops", "Bench Hop-Overs", "Lateral hops over the bench", ExerciseStation.FLOOR, listOf("Full Body", "Core"), null, 30,
            formTips = listOf("Hands firmly on bench, shoulder-width", "Keep core tight as you hop", "Land softly on balls of feet")),
        Exercise("floor_pop_squats", "Pop Squats", "Explosive squat jumps", ExerciseStation.FLOOR, listOf("Quads", "Glutes", "Calves"), null, 30,
            formTips = listOf("Land softly with bent knees", "Jump from a deep squat position", "Arms help generate upward momentum"))
    ).associateBy { it.id }

    fun getAll(): List<Exercise> = exercises.values.toList()

    fun getById(id: String): Exercise? = exercises[id]

    fun getByStation(station: ExerciseStation): List<Exercise> =
        exercises.values.filter { it.station == station }
}
