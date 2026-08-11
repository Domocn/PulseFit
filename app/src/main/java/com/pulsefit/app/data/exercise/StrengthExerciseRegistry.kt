package com.pulsefit.app.data.exercise

import com.pulsefit.app.data.model.BodyRegion
import com.pulsefit.app.data.model.Equipment
import com.pulsefit.app.data.model.ExerciseCategory
import com.pulsefit.app.data.model.MuscleGroup
import com.pulsefit.app.data.model.StrengthExercise
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory registry of 80+ strength training exercises.
 * Organized by muscle group. Each exercise includes form tips,
 * equipment requirements, difficulty rating, and instructions.
 *
 * Mirrors the pattern of the existing ExerciseRegistry for cardio exercises.
 */
@Singleton
class StrengthExerciseRegistry @Inject constructor() {

    private val exercises: Map<String, StrengthExercise> = listOf(
        // ── CHEST (8) ──────────────────────────────────────────
        StrengthExercise(
            id = "chest_barbell_bench",
            name = "Barbell Bench Press",
            primaryMuscleGroup = MuscleGroup.CHEST,
            secondaryMuscleGroups = listOf(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.BARBELL, Equipment.BENCH),
            difficulty = 3,
            instructions = "Lie flat on bench, grip bar slightly wider than shoulder-width. Lower bar to mid-chest, then press up to full arm extension.",
            formTips = listOf(
                "Keep feet flat on floor",
                "Maintain a slight arch in lower back",
                "Tuck elbows at ~45° to protect shoulders",
                "Touch bar to chest, don't bounce"
            )
        ),
        StrengthExercise(
            id = "chest_dumbbell_press",
            name = "Dumbbell Press",
            primaryMuscleGroup = MuscleGroup.CHEST,
            secondaryMuscleGroups = listOf(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.DUMBBELLS, Equipment.BENCH),
            difficulty = 2,
            instructions = "Lie on bench holding dumbbells at chest level. Press up until arms are extended, then lower with control.",
            formTips = listOf(
                "Palms face forward",
                "Don't let dumbbells touch at the top",
                "Full range of motion — dumbbells to chest level",
                "Greater ROM than barbell for chest stretch"
            )
        ),
        StrengthExercise(
            id = "chest_incline_press",
            name = "Incline Dumbbell Press",
            primaryMuscleGroup = MuscleGroup.CHEST,
            secondaryMuscleGroups = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            equipment = listOf(Equipment.DUMBBELLS, Equipment.BENCH),
            difficulty = 2,
            instructions = "Set bench to 30-45° incline. Press dumbbells from chest level to full extension above upper chest.",
            formTips = listOf(
                "Targets upper chest fibres",
                "Don't set incline too steep (>45° shifts to shoulders)",
                "Keep shoulders pinned back",
                "Control the descent"
            )
        ),
        StrengthExercise(
            id = "chest_cable_flyes",
            name = "Cable Flyes",
            primaryMuscleGroup = MuscleGroup.CHEST,
            secondaryMuscleGroups = listOf(MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.CABLE_MACHINE),
            difficulty = 2,
            instructions = "Set cables to shoulder height. With slight elbow bend, bring handles together in an arc in front of chest.",
            formTips = listOf(
                "Keep a slight bend in elbows throughout",
                "Squeeze chest at the peak contraction",
                "Don't use momentum — slow and controlled",
                "Imagine hugging a large tree"
            )
        ),
        StrengthExercise(
            id = "chest_dips",
            name = "Chest Dips",
            primaryMuscleGroup = MuscleGroup.CHEST,
            secondaryMuscleGroups = listOf(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.BODYWEIGHT),
            difficulty = 3,
            instructions = "On parallel bars, lean forward slightly, lower body until shoulders are below elbows, then push back up.",
            formTips = listOf(
                "Lean forward to emphasise chest",
                "Don't go too deep — stop when you feel shoulder stretch",
                "Lock out at the top",
                "Add weight via dip belt when bodyweight is easy"
            ),
            isBodyweight = true
        ),
        StrengthExercise(
            id = "chest_push_ups",
            name = "Push-Ups",
            primaryMuscleGroup = MuscleGroup.CHEST,
            secondaryMuscleGroups = listOf(MuscleGroup.TRICEPS, MuscleGroup.CORE),
            equipment = listOf(Equipment.BODYWEIGHT),
            difficulty = 1,
            instructions = "Hands slightly wider than shoulders, body in straight line. Lower chest to floor, push back up.",
            formTips = listOf(
                "Body straight — no sagging hips",
                "Elbows at ~45° from body",
                "Full range: chest to floor",
                "Elevate feet for extra difficulty"
            ),
            isBodyweight = true
        ),
        StrengthExercise(
            id = "chest_decline_press",
            name = "Decline Bench Press",
            primaryMuscleGroup = MuscleGroup.CHEST,
            secondaryMuscleGroups = listOf(MuscleGroup.TRICEPS),
            equipment = listOf(Equipment.BARBELL, Equipment.BENCH),
            difficulty = 3,
            instructions = "Lie on decline bench, secure legs. Grip bar wider than shoulders, lower to lower chest, press up.",
            formTips = listOf(
                "Targets lower chest fibres",
                "Use a spotter for heavy sets",
                "Don't bounce bar off chest",
                "Keep wrists straight"
            )
        ),
        StrengthExercise(
            id = "chest_machine_press",
            name = "Machine Chest Press",
            primaryMuscleGroup = MuscleGroup.CHEST,
            secondaryMuscleGroups = listOf(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.MACHINE),
            difficulty = 1,
            instructions = "Sit at machine, grip handles at chest level. Press forward to full extension, return with control.",
            formTips = listOf(
                "Good for beginners — fixed path",
                "Adjust seat so handles align with mid-chest",
                "Don't lock elbows forcefully",
                "Squeeze chest at full extension"
            )
        ),

        // ── BACK (10) ──────────────────────────────────────────
        StrengthExercise(
            id = "back_deadlift",
            name = "Deadlift",
            primaryMuscleGroup = MuscleGroup.BACK,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS, MuscleGroup.LOWER_BACK),
            equipment = listOf(Equipment.BARBELL),
            difficulty = 4,
            instructions = "Stand with feet hip-width, bar over mid-foot. Hinge at hips, grip bar, drive through heels to stand tall.",
            formTips = listOf(
                "Bar stays close to shins and thighs",
                "Keep back flat — no rounding",
                "Drive hips forward at the top, don't lean back",
                "Brace core before each rep"
            )
        ),
        StrengthExercise(
            id = "back_pull_ups",
            name = "Pull-Ups",
            primaryMuscleGroup = MuscleGroup.BACK,
            secondaryMuscleGroups = listOf(MuscleGroup.BICEPS),
            equipment = listOf(Equipment.PULL_UP_BAR),
            difficulty = 4,
            instructions = "Grip bar with palms facing away, slightly wider than shoulders. Pull up until chin clears the bar.",
            formTips = listOf(
                "Start from dead hang — full extension",
                "Pull shoulder blades down and together",
                "Avoid kipping/swinging",
                "Use assisted machine or bands if needed"
            ),
            isBodyweight = true
        ),
        StrengthExercise(
            id = "back_barbell_row",
            name = "Barbell Row",
            primaryMuscleGroup = MuscleGroup.BACK,
            secondaryMuscleGroups = listOf(MuscleGroup.BICEPS, MuscleGroup.LOWER_BACK),
            equipment = listOf(Equipment.BARBELL),
            difficulty = 3,
            instructions = "Hinge forward at hips (~45°), grip bar shoulder-width. Pull bar to lower chest, squeeze shoulder blades.",
            formTips = listOf(
                "Keep back flat throughout",
                "Pull with elbows, not biceps",
                "Don't use momentum — controlled reps",
                "Bar path: straight up to lower chest"
            )
        ),
        StrengthExercise(
            id = "back_dumbbell_row",
            name = "Dumbbell Row",
            primaryMuscleGroup = MuscleGroup.BACK,
            secondaryMuscleGroups = listOf(MuscleGroup.BICEPS),
            equipment = listOf(Equipment.DUMBBELLS, Equipment.BENCH),
            difficulty = 2,
            instructions = "Place one knee and hand on bench. Pull dumbbell from floor to hip, squeezing back muscles.",
            formTips = listOf(
                "Keep torso parallel to floor",
                "Pull elbow back, not out to side",
                "Squeeze at the top for 1 second",
                "Don't rotate torso"
            )
        ),
        StrengthExercise(
            id = "back_lat_pulldown",
            name = "Lat Pulldown",
            primaryMuscleGroup = MuscleGroup.BACK,
            secondaryMuscleGroups = listOf(MuscleGroup.BICEPS),
            equipment = listOf(Equipment.CABLE_MACHINE),
            difficulty = 2,
            instructions = "Grip wide bar, sit with thighs secured. Pull bar to upper chest, squeeze lats, return with control.",
            formTips = listOf(
                "Lean back slightly (~15°)",
                "Pull elbows down and back",
                "Don't use bodyweight to pull — controlled",
                "Full stretch at the top"
            )
        ),
        StrengthExercise(
            id = "back_cable_row",
            name = "Seated Cable Row",
            primaryMuscleGroup = MuscleGroup.BACK,
            secondaryMuscleGroups = listOf(MuscleGroup.BICEPS),
            equipment = listOf(Equipment.CABLE_MACHINE),
            difficulty = 2,
            instructions = "Sit at cable row, feet on platform. Pull handle to abdomen, squeeze shoulder blades, extend arms with control.",
            formTips = listOf(
                "Keep back straight — don't round forward",
                "Pull with elbows, squeeze at peak",
                "Don't use momentum from legs",
                "Vary grip (narrow/wide) for different emphasis"
            )
        ),
        StrengthExercise(
            id = "back_face_pulls",
            name = "Face Pulls",
            primaryMuscleGroup = MuscleGroup.BACK,
            secondaryMuscleGroups = listOf(MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.CABLE_MACHINE),
            difficulty = 1,
            instructions = "Set cable to face height. Pull rope towards face, separating hands and squeezing rear delts and upper back.",
            formTips = listOf(
                "Excellent for shoulder health and posture",
                "Pull to eye level, hands end beside ears",
                "External rotation at the end",
                "Light weight, high reps (12-20)"
            )
        ),
        StrengthExercise(
            id = "back_tbar_row",
            name = "T-Bar Row",
            primaryMuscleGroup = MuscleGroup.BACK,
            secondaryMuscleGroups = listOf(MuscleGroup.BICEPS, MuscleGroup.LOWER_BACK),
            equipment = listOf(Equipment.BARBELL, Equipment.LANDMINE),
            difficulty = 3,
            instructions = "Straddle the bar, hinge forward, grip handle. Pull weight to chest, squeeze back, lower with control.",
            formTips = listOf(
                "Keep back flat — brace core",
                "Pull to lower chest",
                "Squeeze shoulder blades at top",
                "Great mid-back builder"
            )
        ),
        StrengthExercise(
            id = "back_good_mornings",
            name = "Good Mornings",
            primaryMuscleGroup = MuscleGroup.LOWER_BACK,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            equipment = listOf(Equipment.BARBELL),
            difficulty = 3,
            instructions = "Place bar across upper back. Hinge at hips, lowering torso until nearly parallel. Return to standing.",
            formTips = listOf(
                "Keep back flat — never round",
                "Soft knee bend, not locked",
                "Hinge at hips, not waist",
                "Start light — form is critical"
            )
        ),
        StrengthExercise(
            id = "back_rack_pulls",
            name = "Rack Pulls",
            primaryMuscleGroup = MuscleGroup.BACK,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            equipment = listOf(Equipment.BARBELL),
            difficulty = 3,
            instructions = "Set bar at knee height in rack. Grip bar, drive hips forward to stand tall. Partial ROM deadlift.",
            formTips = listOf(
                "Overloads the top portion of deadlift",
                "Great for grip strength",
                "Keep back flat",
                "Drive hips through — don't lean back"
            )
        ),

        // ── SHOULDERS (8) ──────────────────────────────────────
        StrengthExercise(
            id = "shoulders_ohp",
            name = "Overhead Press",
            primaryMuscleGroup = MuscleGroup.SHOULDERS,
            secondaryMuscleGroups = listOf(MuscleGroup.TRICEPS),
            equipment = listOf(Equipment.BARBELL),
            difficulty = 3,
            instructions = "Stand with bar at collarbone, grip shoulder-width. Press bar overhead to full lockout, lower with control.",
            formTips = listOf(
                "Brace core — don't arch lower back",
                "Bar path: straight up, head moves back then through",
                "Full lockout at the top",
                "Keep wrists straight"
            )
        ),
        StrengthExercise(
            id = "shoulders_lateral_raises",
            name = "Lateral Raises",
            primaryMuscleGroup = MuscleGroup.SHOULDERS,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.DUMBBELLS),
            difficulty = 1,
            instructions = "Stand holding dumbbells at sides. Raise arms out to sides until parallel to floor, lower with control.",
            formTips = listOf(
                "Slight bend in elbows",
                "Lead with elbows, not hands",
                "Don't swing — controlled movement",
                "Pause at the top for 1 second"
            )
        ),
        StrengthExercise(
            id = "shoulders_front_raises",
            name = "Front Raises",
            primaryMuscleGroup = MuscleGroup.SHOULDERS,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.DUMBBELLS),
            difficulty = 1,
            instructions = "Hold dumbbells in front of thighs. Raise one or both arms to shoulder height, lower with control.",
            formTips = listOf(
                "Don't swing — no momentum",
                "Raise to shoulder height only",
                "Alternate arms or raise together",
                "Thumbs can point up or forward"
            )
        ),
        StrengthExercise(
            id = "shoulders_arnold_press",
            name = "Arnold Press",
            primaryMuscleGroup = MuscleGroup.SHOULDERS,
            secondaryMuscleGroups = listOf(MuscleGroup.TRICEPS),
            equipment = listOf(Equipment.DUMBBELLS),
            difficulty = 2,
            instructions = "Start with dumbbells at chin height, palms facing you. As you press up, rotate palms to face forward.",
            formTips = listOf(
                "Rotation engages all three delt heads",
                "Full ROM — dumbbells nearly touch at top",
                "Reverse the rotation on the way down",
                "Sit on bench with back support for stability"
            )
        ),
        StrengthExercise(
            id = "shoulders_reverse_flyes",
            name = "Reverse Flyes",
            primaryMuscleGroup = MuscleGroup.SHOULDERS,
            secondaryMuscleGroups = listOf(MuscleGroup.BACK),
            equipment = listOf(Equipment.DUMBBELLS),
            difficulty = 2,
            instructions = "Hinge forward at hips, dumbbells hanging. Raise arms out to sides, squeezing rear delts and upper back.",
            formTips = listOf(
                "Keep back flat — hinge at hips",
                "Slight elbow bend",
                "Squeeze shoulder blades at top",
                "Light weight, focus on rear delts"
            )
        ),
        StrengthExercise(
            id = "shoulders_upright_rows",
            name = "Upright Rows",
            primaryMuscleGroup = MuscleGroup.SHOULDERS,
            secondaryMuscleGroups = listOf(MuscleGroup.BACK),
            equipment = listOf(Equipment.BARBELL, Equipment.EZ_BAR),
            difficulty = 2,
            instructions = "Grip bar narrower than shoulder-width. Pull bar up along body to chin height, elbows flaring out.",
            formTips = listOf(
                "Keep bar close to body",
                "Elbows lead the movement",
                "Don't pull higher than chin — shoulder impingement risk",
                "EZ bar or dumbbells can be easier on wrists"
            )
        ),
        StrengthExercise(
            id = "shoulders_shrugs",
            name = "Barbell Shrugs",
            primaryMuscleGroup = MuscleGroup.SHOULDERS,
            secondaryMuscleGroups = listOf(MuscleGroup.BACK),
            equipment = listOf(Equipment.BARBELL),
            difficulty = 1,
            instructions = "Hold barbell at hip level, shoulder-width grip. Shrug shoulders up towards ears, hold, lower with control.",
            formTips = listOf(
                "Full ROM: let shoulders drop fully between reps",
                "Don't roll shoulders — straight up and down",
                "Heavy weight OK for traps",
                "Hold at top for 1-2 seconds"
            )
        ),
        StrengthExercise(
            id = "shoulders_machine_press",
            name = "Machine Shoulder Press",
            primaryMuscleGroup = MuscleGroup.SHOULDERS,
            secondaryMuscleGroups = listOf(MuscleGroup.TRICEPS),
            equipment = listOf(Equipment.MACHINE),
            difficulty = 1,
            instructions = "Sit at shoulder press machine, grip handles at shoulder height. Press up to near-lockout, lower with control.",
            formTips = listOf(
                "Adjust seat so handles align with shoulders",
                "Don't lock elbows forcefully",
                "Fixed path — good for beginners",
                "Keep back against pad"
            )
        ),

        // ── BICEPS (6) ─────────────────────────────────────────
        StrengthExercise(
            id = "biceps_barbell_curl",
            name = "Barbell Curl",
            primaryMuscleGroup = MuscleGroup.BICEPS,
            secondaryMuscleGroups = listOf(MuscleGroup.FOREARMS),
            equipment = listOf(Equipment.BARBELL),
            difficulty = 1,
            instructions = "Stand holding barbell with underhand grip, shoulder-width. Curl bar to shoulders, squeeze biceps, lower with control.",
            formTips = listOf(
                "Keep elbows pinned to sides",
                "No swinging — strict form",
                "Full ROM: full stretch to full contraction",
                "EZ bar variant reduces wrist strain"
            )
        ),
        StrengthExercise(
            id = "biceps_dumbbell_curl",
            name = "Dumbbell Curl",
            primaryMuscleGroup = MuscleGroup.BICEPS,
            secondaryMuscleGroups = listOf(MuscleGroup.FOREARMS),
            equipment = listOf(Equipment.DUMBBELLS),
            difficulty = 1,
            instructions = "Hold dumbbells at sides, palms forward. Curl to shoulders, squeeze, lower with control.",
            formTips = listOf(
                "Alternate arms or curl together",
                "Supinate (rotate palm up) for extra contraction",
                "Don't swing — keep body still",
                "Control the negative (lowering) phase"
            )
        ),
        StrengthExercise(
            id = "biceps_hammer_curl",
            name = "Hammer Curl",
            primaryMuscleGroup = MuscleGroup.BICEPS,
            secondaryMuscleGroups = listOf(MuscleGroup.FOREARMS),
            equipment = listOf(Equipment.DUMBBELLS),
            difficulty = 1,
            instructions = "Hold dumbbells at sides, palms facing each other (neutral grip). Curl to shoulders, squeeze, lower.",
            formTips = listOf(
                "Targets brachialis and brachioradialis",
                "Neutral grip throughout — don't rotate",
                "Great for forearm development",
                "Can curl both arms together"
            )
        ),
        StrengthExercise(
            id = "biceps_preacher_curl",
            name = "Preacher Curl",
            primaryMuscleGroup = MuscleGroup.BICEPS,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.BARBELL, Equipment.EZ_BAR, Equipment.BENCH),
            difficulty = 2,
            instructions = "Sit at preacher bench, arms over pad. Curl bar to shoulders, squeeze at top, lower to full extension.",
            formTips = listOf(
                "Eliminates momentum — strict isolation",
                "Full stretch at the bottom",
                "Don't hyperextend elbows at bottom",
                "EZ bar is gentler on wrists"
            )
        ),
        StrengthExercise(
            id = "biceps_cable_curl",
            name = "Cable Curl",
            primaryMuscleGroup = MuscleGroup.BICEPS,
            secondaryMuscleGroups = listOf(MuscleGroup.FOREARMS),
            equipment = listOf(Equipment.CABLE_MACHINE),
            difficulty = 1,
            instructions = "Stand facing low cable with straight bar attachment. Curl to shoulders, squeeze, lower with control.",
            formTips = listOf(
                "Constant tension throughout ROM",
                "Keep elbows at sides",
                "Squeeze at peak contraction",
                "Try rope attachment for variety"
            )
        ),
        StrengthExercise(
            id = "biceps_concentration_curl",
            name = "Concentration Curl",
            primaryMuscleGroup = MuscleGroup.BICEPS,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.DUMBBELLS),
            difficulty = 1,
            instructions = "Sit on bench, elbow against inner thigh. Curl dumbbell to shoulder, squeeze biceps hard, lower slowly.",
            formTips = listOf(
                "Maximum isolation — no body movement",
                "Squeeze and hold at peak for 1-2 seconds",
                "Full ROM: arm fully extended at bottom",
                "Great finisher exercise"
            )
        ),

        // ── TRICEPS (6) ────────────────────────────────────────
        StrengthExercise(
            id = "triceps_close_grip_bench",
            name = "Close-Grip Bench Press",
            primaryMuscleGroup = MuscleGroup.TRICEPS,
            secondaryMuscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.BARBELL, Equipment.BENCH),
            difficulty = 3,
            instructions = "Lie on bench, grip bar shoulder-width or slightly narrower. Lower to lower chest, press up focusing on triceps.",
            formTips = listOf(
                "Elbows stay close to body",
                "Don't grip too narrow — wrist strain risk",
                "Lower to lower chest, not neck",
                "Lock out with triceps, not chest"
            )
        ),
        StrengthExercise(
            id = "triceps_pushdown",
            name = "Tricep Pushdown",
            primaryMuscleGroup = MuscleGroup.TRICEPS,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.CABLE_MACHINE),
            difficulty = 1,
            instructions = "Stand at high cable with straight bar or rope. Push down until arms are fully extended, squeeze triceps.",
            formTips = listOf(
                "Keep elbows pinned to sides",
                "Only forearms move — upper arms stay still",
                "Full lockout at bottom, squeeze",
                "Don't lean into the weight"
            )
        ),
        StrengthExercise(
            id = "triceps_overhead_extension",
            name = "Overhead Tricep Extension",
            primaryMuscleGroup = MuscleGroup.TRICEPS,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.DUMBBELLS, Equipment.CABLE_MACHINE),
            difficulty = 2,
            instructions = "Hold dumbbell overhead with both hands. Lower behind head, then extend arms back to starting position.",
            formTips = listOf(
                "Keep elbows close to ears",
                "Only forearms move",
                "Don't flare elbows out",
                "Can do seated with back support"
            )
        ),
        StrengthExercise(
            id = "triceps_skull_crushers",
            name = "Skull Crushers",
            primaryMuscleGroup = MuscleGroup.TRICEPS,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.EZ_BAR, Equipment.BENCH),
            difficulty = 2,
            instructions = "Lie on bench, hold EZ bar above chest with arms extended. Lower bar towards forehead by bending elbows, extend back.",
            formTips = listOf(
                "Keep upper arms vertical — don't let them drift",
                "Lower to forehead, not past it",
                "EZ bar is easier on wrists than straight bar",
                "Use a spotter for heavy sets"
            )
        ),
        StrengthExercise(
            id = "triceps_dips",
            name = "Tricep Dips",
            primaryMuscleGroup = MuscleGroup.TRICEPS,
            secondaryMuscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.BODYWEIGHT),
            difficulty = 3,
            instructions = "On parallel bars, keep body upright. Lower until elbows are at 90°, push back up to lockout.",
            formTips = listOf(
                "Stay upright to emphasise triceps (lean = chest)",
                "Don't go too deep — shoulder strain risk",
                "Full lockout at top",
                "Add weight via dip belt for progression"
            ),
            isBodyweight = true
        ),
        StrengthExercise(
            id = "triceps_kickbacks",
            name = "Tricep Kickbacks",
            primaryMuscleGroup = MuscleGroup.TRICEPS,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.DUMBBELLS),
            difficulty = 1,
            instructions = "Hinge forward, one knee and hand on bench. Extend dumbbell back until arm is straight, squeeze triceps.",
            formTips = listOf(
                "Keep upper arm parallel to floor",
                "Only forearm moves",
                "Squeeze and hold at full extension",
                "Light weight, high reps — focus on contraction"
            )
        ),

        // ── LEGS (12) ──────────────────────────────────────────
        StrengthExercise(
            id = "legs_squat",
            name = "Barbell Squat",
            primaryMuscleGroup = MuscleGroup.QUADS,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS, MuscleGroup.CORE),
            equipment = listOf(Equipment.BARBELL),
            difficulty = 4,
            instructions = "Place bar across upper back. Squat down until thighs are at least parallel, drive through heels to stand.",
            formTips = listOf(
                "Knees track over toes — don't cave in",
                "Chest up, back flat",
                "Depth: at least parallel (hip crease below knee)",
                "Brace core before each rep"
            )
        ),
        StrengthExercise(
            id = "legs_front_squat",
            name = "Front Squat",
            primaryMuscleGroup = MuscleGroup.QUADS,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES, MuscleGroup.CORE),
            equipment = listOf(Equipment.BARBELL),
            difficulty = 4,
            instructions = "Rest bar across front delts, elbows high. Squat down keeping torso upright, drive through heels.",
            formTips = listOf(
                "Keep elbows high — don't let them drop",
                "More quad-dominant than back squat",
                "Requires good wrist/ankle mobility",
                "Cross-arm grip alternative if wrists are tight"
            )
        ),
        StrengthExercise(
            id = "legs_leg_press",
            name = "Leg Press",
            primaryMuscleGroup = MuscleGroup.QUADS,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            equipment = listOf(Equipment.MACHINE),
            difficulty = 1,
            instructions = "Sit in leg press, feet shoulder-width on platform. Lower until knees are at ~90°, press back to start.",
            formTips = listOf(
                "Don't lock knees at the top",
                "Lower back stays flat on pad",
                "Foot position changes emphasis (high = glutes, low = quads)",
                "Full ROM — don't do half reps"
            )
        ),
        StrengthExercise(
            id = "legs_lunges",
            name = "Dumbbell Lunges",
            primaryMuscleGroup = MuscleGroup.QUADS,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            equipment = listOf(Equipment.DUMBBELLS),
            difficulty = 2,
            instructions = "Hold dumbbells at sides. Step forward, lower until both knees are at 90°, push back to start.",
            formTips = listOf(
                "Front knee stays over ankle",
                "Back knee nearly touches floor",
                "Keep torso upright",
                "Alternate legs or do all reps on one side"
            )
        ),
        StrengthExercise(
            id = "legs_bulgarian_split_squat",
            name = "Bulgarian Split Squat",
            primaryMuscleGroup = MuscleGroup.QUADS,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            equipment = listOf(Equipment.DUMBBELLS, Equipment.BENCH),
            difficulty = 3,
            instructions = "Place back foot on bench behind you. Lower until front thigh is parallel, drive through front heel.",
            formTips = listOf(
                "Front knee stays over ankle",
                "Keep torso upright",
                "Great for fixing muscle imbalances",
                "Start bodyweight, add dumbbells as you progress"
            )
        ),
        StrengthExercise(
            id = "legs_leg_extension",
            name = "Leg Extension",
            primaryMuscleGroup = MuscleGroup.QUADS,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.MACHINE),
            difficulty = 1,
            instructions = "Sit at leg extension machine, pad at shin level. Extend legs to full lockout, squeeze quads, lower with control.",
            formTips = listOf(
                "Squeeze quads at full extension",
                "Don't swing — controlled throughout",
                "Adjust pad to just above ankles",
                "Great isolation/finisher for quads"
            )
        ),
        StrengthExercise(
            id = "legs_leg_curl",
            name = "Leg Curl",
            primaryMuscleGroup = MuscleGroup.HAMSTRINGS,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.MACHINE),
            difficulty = 1,
            instructions = "Lie face down on leg curl machine, pad at heel level. Curl legs towards glutes, squeeze, lower with control.",
            formTips = listOf(
                "Don't let hips lift off pad",
                "Squeeze hamstrings at peak contraction",
                "Full ROM: full stretch to full contraction",
                "Seated variant also effective"
            )
        ),
        StrengthExercise(
            id = "legs_rdl",
            name = "Romanian Deadlift",
            primaryMuscleGroup = MuscleGroup.HAMSTRINGS,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES, MuscleGroup.LOWER_BACK),
            equipment = listOf(Equipment.BARBELL, Equipment.DUMBBELLS),
            difficulty = 3,
            instructions = "Hold bar at hip level. Hinge at hips, pushing them back, lower bar along legs until you feel hamstring stretch.",
            formTips = listOf(
                "Soft knee bend — don't squat",
                "Keep bar close to legs",
                "Back stays flat throughout",
                "Go only as low as hamstring flexibility allows"
            )
        ),
        StrengthExercise(
            id = "legs_hip_thrust",
            name = "Hip Thrust",
            primaryMuscleGroup = MuscleGroup.GLUTES,
            secondaryMuscleGroups = listOf(MuscleGroup.HAMSTRINGS),
            equipment = listOf(Equipment.BARBELL, Equipment.BENCH),
            difficulty = 2,
            instructions = "Upper back against bench, bar across hips. Drive hips up until body is straight, squeeze glutes at top.",
            formTips = listOf(
                "Chin tucked — look forward, not up",
                "Drive through heels",
                "Squeeze glutes hard at the top for 1-2 seconds",
                "Use pad on bar for comfort"
            )
        ),
        StrengthExercise(
            id = "legs_calf_raises",
            name = "Standing Calf Raises",
            primaryMuscleGroup = MuscleGroup.CALVES,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.MACHINE, Equipment.DUMBBELLS),
            difficulty = 1,
            instructions = "Stand with balls of feet on platform edge. Rise onto toes as high as possible, lower heels below platform level.",
            formTips = listOf(
                "Full ROM: full stretch to full contraction",
                "Pause at top and bottom",
                "Don't bounce — controlled reps",
                "High reps (15-25) for calves"
            )
        ),
        StrengthExercise(
            id = "legs_hack_squat",
            name = "Hack Squat",
            primaryMuscleGroup = MuscleGroup.QUADS,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES),
            equipment = listOf(Equipment.MACHINE),
            difficulty = 2,
            instructions = "Stand on hack squat platform, shoulders under pads. Lower until knees are at ~90°, press through heels to stand.",
            formTips = listOf(
                "Keep back flat against pad",
                "Don't lock knees at top",
                "Foot position changes quad emphasis",
                "Great alternative when barbell squat isn't available"
            )
        ),
        StrengthExercise(
            id = "legs_step_ups",
            name = "Dumbbell Step-Ups",
            primaryMuscleGroup = MuscleGroup.QUADS,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            equipment = listOf(Equipment.DUMBBELLS, Equipment.BENCH),
            difficulty = 2,
            instructions = "Hold dumbbells, place one foot on bench/box. Drive through that foot to stand up, lower with control.",
            formTips = listOf(
                "Don't push off the bottom foot",
                "Keep torso upright",
                "Full hip extension at the top",
                "Start with lower box, progress to higher"
            )
        ),

        // ── CORE (6) ───────────────────────────────────────────
        StrengthExercise(
            id = "core_plank",
            name = "Plank",
            primaryMuscleGroup = MuscleGroup.CORE,
            secondaryMuscleGroups = listOf(MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.BODYWEIGHT),
            difficulty = 1,
            instructions = "Forearms on floor, elbows under shoulders. Hold body in straight line from head to heels.",
            formTips = listOf(
                "Squeeze glutes and brace abs",
                "Don't let hips sag or pike up",
                "Look at floor — neutral neck",
                "Build up time gradually (30s → 2min+)"
            ),
            isBodyweight = true
        ),
        StrengthExercise(
            id = "core_crunches",
            name = "Crunches",
            primaryMuscleGroup = MuscleGroup.CORE,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.BODYWEIGHT),
            difficulty = 1,
            instructions = "Lie on back, knees bent, hands behind head. Curl shoulders off floor, squeeze abs, lower with control.",
            formTips = listOf(
                "Don't pull on neck",
                "Small movement — shoulders only off floor",
                "Exhale on the way up",
                "Focus on contraction, not speed"
            ),
            isBodyweight = true
        ),
        StrengthExercise(
            id = "core_hanging_leg_raises",
            name = "Hanging Leg Raises",
            primaryMuscleGroup = MuscleGroup.CORE,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.PULL_UP_BAR),
            difficulty = 4,
            instructions = "Hang from pull-up bar. Raise legs (straight or bent knees) until parallel to floor or higher, lower with control.",
            formTips = listOf(
                "Don't swing — control the movement",
                "Bend knees if straight legs are too hard",
                "Lower slowly — don't drop",
                "Engage lats to stabilise"
            ),
            isBodyweight = true
        ),
        StrengthExercise(
            id = "core_russian_twists",
            name = "Russian Twists",
            primaryMuscleGroup = MuscleGroup.CORE,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.BODYWEIGHT),
            difficulty = 2,
            instructions = "Sit with knees bent, lean back slightly. Rotate torso side to side, optionally holding a weight.",
            formTips = listOf(
                "Keep spine straight — don't round",
                "Rotate from waist, not arms",
                "Feet can be on floor or elevated",
                "Add dumbbell or medicine ball for resistance"
            ),
            isBodyweight = true
        ),
        StrengthExercise(
            id = "core_cable_crunches",
            name = "Cable Crunches",
            primaryMuscleGroup = MuscleGroup.CORE,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.CABLE_MACHINE),
            difficulty = 2,
            instructions = "Kneel facing high cable with rope attachment. Crunch down, bringing elbows towards knees, squeeze abs.",
            formTips = listOf(
                "Hips stay still — only torso moves",
                "Round your back like a crunch",
                "Squeeze at the bottom",
                "Can load heavier than bodyweight crunches"
            )
        ),
        StrengthExercise(
            id = "core_ab_wheel",
            name = "Ab Wheel Rollout",
            primaryMuscleGroup = MuscleGroup.CORE,
            secondaryMuscleGroups = listOf(MuscleGroup.SHOULDERS, MuscleGroup.BACK),
            equipment = listOf(Equipment.BODYWEIGHT),
            difficulty = 3,
            instructions = "Kneel holding ab wheel. Roll forward extending body, keeping core tight. Pull back to starting position.",
            formTips = listOf(
                "Don't let lower back sag",
                "Start with short range, build up",
                "Brace core throughout",
                "Anti-extension exercise — resists arching"
            ),
            isBodyweight = true
        ),

        // ── FULL BODY (4) ──────────────────────────────────────
        StrengthExercise(
            id = "full_clean_press",
            name = "Clean & Press",
            primaryMuscleGroup = MuscleGroup.FULL_BODY,
            secondaryMuscleGroups = listOf(MuscleGroup.SHOULDERS, MuscleGroup.QUADS, MuscleGroup.GLUTES),
            equipment = listOf(Equipment.BARBELL),
            difficulty = 5,
            instructions = "Pull bar from floor to shoulders (clean), then press overhead. Lower with control and repeat.",
            formTips = listOf(
                "Explosive hip drive for the clean",
                "Catch bar at front rack position",
                "Press from front rack to overhead",
                "Complex movement — master components separately first"
            )
        ),
        StrengthExercise(
            id = "full_burpees",
            name = "Burpees",
            primaryMuscleGroup = MuscleGroup.FULL_BODY,
            secondaryMuscleGroups = listOf(MuscleGroup.QUADS, MuscleGroup.CHEST, MuscleGroup.CORE),
            equipment = listOf(Equipment.BODYWEIGHT),
            difficulty = 2,
            instructions = "From standing, squat down, kick feet back to plank, do a push-up, jump feet back in, explode up into a jump.",
            formTips = listOf(
                "Land softly on the jump",
                "Keep core tight in plank position",
                "Modify: step back instead of jump, skip push-up",
                "Great conditioning exercise"
            ),
            isBodyweight = true,
            category = ExerciseCategory.CARDIO
        ),
        StrengthExercise(
            id = "full_kettlebell_swings",
            name = "Kettlebell Swings",
            primaryMuscleGroup = MuscleGroup.FULL_BODY,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS, MuscleGroup.CORE),
            equipment = listOf(Equipment.KETTLEBELL),
            difficulty = 2,
            instructions = "Hinge at hips, swing kettlebell between legs, then drive hips forward to swing to chest height. Arms are just ropes.",
            formTips = listOf(
                "Power comes from hips, not arms",
                "Snap hips forward — glutes and hamstrings",
                "Keep core braced",
                "Bell should float at the top, not be lifted by arms"
            )
        ),
        StrengthExercise(
            id = "full_turkish_getup",
            name = "Turkish Get-Up",
            primaryMuscleGroup = MuscleGroup.FULL_BODY,
            secondaryMuscleGroups = listOf(MuscleGroup.SHOULDERS, MuscleGroup.CORE, MuscleGroup.GLUTES),
            equipment = listOf(Equipment.KETTLEBELL),
            difficulty = 4,
            instructions = "Lie on back holding kettlebell overhead. Stand up step by step while keeping the bell overhead, then reverse to lie back down.",
            formTips = listOf(
                "Keep eyes on the bell at all times",
                "Arm stays locked out — vertical throughout",
                "Move slowly through each step",
                "Master bodyweight first, then light weight"
            )
        ),

        // ── ADDITIONAL EXERCISES ───────────────────────────────
        // Chest
        StrengthExercise(
            id = "chest_pec_deck",
            name = "Pec Deck Flyes",
            primaryMuscleGroup = MuscleGroup.CHEST,
            secondaryMuscleGroups = listOf(MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.MACHINE),
            difficulty = 1,
            instructions = "Sit at pec deck, arms on pads at 90°. Bring pads together in front of chest, squeeze, return with control.",
            formTips = listOf("Keep back flat against pad", "Squeeze at peak contraction", "Don't use momentum", "Adjust seat so pads align with chest")
        ),

        // Back
        StrengthExercise(
            id = "back_chin_ups",
            name = "Chin-Ups",
            primaryMuscleGroup = MuscleGroup.BACK,
            secondaryMuscleGroups = listOf(MuscleGroup.BICEPS),
            equipment = listOf(Equipment.PULL_UP_BAR),
            difficulty = 3,
            instructions = "Grip bar with palms facing you, shoulder-width. Pull up until chin clears bar, lower with control.",
            formTips = listOf("Palms face you (supinated grip)", "More bicep involvement than pull-ups", "Full dead hang at bottom", "Squeeze back at top"),
            isBodyweight = true
        ),
        StrengthExercise(
            id = "back_single_arm_row",
            name = "Single-Arm Cable Row",
            primaryMuscleGroup = MuscleGroup.BACK,
            secondaryMuscleGroups = listOf(MuscleGroup.BICEPS),
            equipment = listOf(Equipment.CABLE_MACHINE),
            difficulty = 1,
            instructions = "Sit sideways to low cable, grip single handle. Pull to hip, squeeze lat, extend with control.",
            formTips = listOf("Great for fixing imbalances", "Rotate torso slightly for full ROM", "Squeeze at peak", "One arm at a time")
        ),

        // Shoulders
        StrengthExercise(
            id = "shoulders_dumbbell_press",
            name = "Dumbbell Shoulder Press",
            primaryMuscleGroup = MuscleGroup.SHOULDERS,
            secondaryMuscleGroups = listOf(MuscleGroup.TRICEPS),
            equipment = listOf(Equipment.DUMBBELLS),
            difficulty = 2,
            instructions = "Sit on bench with back support, dumbbells at shoulder height. Press overhead to near-lockout, lower with control.",
            formTips = listOf("Don't arch lower back", "Palms face forward", "Dumbbells nearly touch at top", "Use back support for stability")
        ),

        // Legs
        StrengthExercise(
            id = "legs_goblet_squat",
            name = "Goblet Squat",
            primaryMuscleGroup = MuscleGroup.QUADS,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES, MuscleGroup.CORE),
            equipment = listOf(Equipment.DUMBBELLS, Equipment.KETTLEBELL),
            difficulty = 1,
            instructions = "Hold dumbbell/kettlebell at chest. Squat down, elbows tracking inside knees, drive through heels to stand.",
            formTips = listOf("Great for beginners learning squat form", "Weight acts as counterbalance", "Go as deep as mobility allows", "Keep chest up")
        ),
        StrengthExercise(
            id = "legs_sumo_deadlift",
            name = "Sumo Deadlift",
            primaryMuscleGroup = MuscleGroup.QUADS,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES, MuscleGroup.BACK),
            equipment = listOf(Equipment.BARBELL),
            difficulty = 3,
            instructions = "Wide stance, toes pointed out, grip bar inside legs. Drive through heels, push knees out, stand tall.",
            formTips = listOf("More quad-dominant than conventional", "Keep knees tracking over toes", "Bar stays close to body", "Shorter ROM than conventional deadlift")
        ),
        StrengthExercise(
            id = "legs_nordic_curls",
            name = "Nordic Hamstring Curls",
            primaryMuscleGroup = MuscleGroup.HAMSTRINGS,
            secondaryMuscleGroups = listOf(MuscleGroup.GLUTES),
            equipment = listOf(Equipment.BODYWEIGHT),
            difficulty = 4,
            instructions = "Kneel with feet secured. Lower body towards floor with control, using hamstrings to resist. Push back up.",
            formTips = listOf("Eccentric focus — lower as slowly as possible", "Use hands to assist if needed", "Keep body straight — hinge at knees only", "Excellent for hamstring strength and injury prevention"),
            isBodyweight = true
        ),

        // Core
        StrengthExercise(
            id = "core_dead_bug",
            name = "Dead Bug",
            primaryMuscleGroup = MuscleGroup.CORE,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.BODYWEIGHT),
            difficulty = 1,
            instructions = "Lie on back, arms and legs up. Extend opposite arm and leg, keeping core braced. Return and alternate.",
            formTips = listOf("Lower back stays pressed into floor", "Move slowly and controlled", "Exhale during extension", "Great for anti-extension core strength"),
            isBodyweight = true
        ),
        StrengthExercise(
            id = "core_pallof_press",
            name = "Pallof Press",
            primaryMuscleGroup = MuscleGroup.CORE,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.CABLE_MACHINE, Equipment.RESISTANCE_BANDS),
            difficulty = 1,
            instructions = "Stand sideways to cable at chest height. Press handle straight out, resisting rotation. Hold, return.",
            formTips = listOf("Anti-rotation — don't let cable pull you", "Keep hips square", "Hold at full extension for 2-3 seconds", "Great for obliques and deep core")
        ),

        // Arms
        StrengthExercise(
            id = "biceps_incline_curl",
            name = "Incline Dumbbell Curl",
            primaryMuscleGroup = MuscleGroup.BICEPS,
            secondaryMuscleGroups = listOf(),
            equipment = listOf(Equipment.DUMBBELLS, Equipment.BENCH),
            difficulty = 2,
            instructions = "Sit on incline bench (~45°), arms hanging straight down. Curl dumbbells to shoulders, squeeze, lower.",
            formTips = listOf("Arms behind body = greater stretch on biceps", "Full ROM — arms fully extended at bottom", "Don't swing", "Great for long head of biceps")
        ),
        StrengthExercise(
            id = "triceps_diamond_pushups",
            name = "Diamond Push-Ups",
            primaryMuscleGroup = MuscleGroup.TRICEPS,
            secondaryMuscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.CORE),
            equipment = listOf(Equipment.BODYWEIGHT),
            difficulty = 2,
            instructions = "Hands together forming diamond shape under chest. Lower chest to hands, push back up focusing on triceps.",
            formTips = listOf("Elbows stay close to body", "Body straight — no sagging", "Hands directly under chest", "Great bodyweight tricep builder"),
            isBodyweight = true
        ),

        // Glutes
        StrengthExercise(
            id = "glutes_cable_kickbacks",
            name = "Cable Kickbacks",
            primaryMuscleGroup = MuscleGroup.GLUTES,
            secondaryMuscleGroups = listOf(MuscleGroup.HAMSTRINGS),
            equipment = listOf(Equipment.CABLE_MACHINE),
            difficulty = 1,
            instructions = "Face cable machine, ankle cuff on low pulley. Kick leg back, squeeze glute at full extension, return with control.",
            formTips = listOf("Keep supporting leg slightly bent", "Don't arch lower back", "Squeeze glute at peak", "Controlled — no swinging")
        ),
        StrengthExercise(
            id = "glutes_bulgarian_split_squat_glute",
            name = "Glute-Focused Bulgarian Split Squat",
            primaryMuscleGroup = MuscleGroup.GLUTES,
            secondaryMuscleGroups = listOf(MuscleGroup.QUADS),
            equipment = listOf(Equipment.DUMBBELLS, Equipment.BENCH),
            difficulty = 3,
            instructions = "Same as Bulgarian split squat but with forward lean and front foot further forward to emphasise glutes.",
            formTips = listOf("Lean torso forward slightly", "Front foot further forward = more glute", "Drive through heel", "Feel the stretch in the glute")
        )
    ).associateBy { it.id }

    fun getAll(): List<StrengthExercise> = exercises.values.toList()

    fun getById(id: String): StrengthExercise? = exercises[id]

    fun getByMuscleGroup(muscleGroup: MuscleGroup): List<StrengthExercise> =
        exercises.values.filter {
            it.primaryMuscleGroup == muscleGroup || it.secondaryMuscleGroups.contains(muscleGroup)
        }

    fun getByBodyRegion(region: BodyRegion): List<StrengthExercise> =
        exercises.values.filter {
            it.primaryMuscleGroup.bodyRegion == region
        }

    fun getByEquipment(equipment: Equipment): List<StrengthExercise> =
        exercises.values.filter { it.equipment.contains(equipment) }

    fun getBodyweightOnly(): List<StrengthExercise> =
        exercises.values.filter { it.isBodyweight }

    fun search(query: String): List<StrengthExercise> {
        val q = query.lowercase()
        return exercises.values.filter {
            it.name.lowercase().contains(q) ||
            it.primaryMuscleGroup.label.lowercase().contains(q) ||
            it.secondaryMuscleGroups.any { m -> m.label.lowercase().contains(q) }
        }
    }

    fun getByCategory(category: ExerciseCategory): List<StrengthExercise> =
        exercises.values.filter { it.category == category }

    fun getMuscleGroups(): List<MuscleGroup> = MuscleGroup.entries.toList()

    fun getEquipment(): List<Equipment> = Equipment.entries.toList()
}
