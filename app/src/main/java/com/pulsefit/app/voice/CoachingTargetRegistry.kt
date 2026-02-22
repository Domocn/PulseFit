package com.pulsefit.app.voice

import com.pulsefit.app.data.model.ExerciseStation
import com.pulsefit.app.data.model.TreadMode
import javax.inject.Inject
import javax.inject.Singleton

data class ExerciseCoachingTarget(
    val exerciseId: String,
    val joggerSpeedMph: Double? = null,
    val runnerSpeedMph: Double? = null,
    val walkerSpeedMph: Double? = null,
    val walkerIncline: Int? = null,
    val rowWatts: Int? = null,
    val formCue: String? = null
)

@Singleton
class CoachingTargetRegistry @Inject constructor() {

    private val targets: Map<String, ExerciseCoachingTarget> = listOf(
        // Tread — speeds are minimum suggested values; inclines for power walkers
        // Jogger: 4.5-5.5 base, 5.5-6.5 push, 7-8 all-out
        // Runner: 5.5-7.5 base, 7-9 push, 8-12 all-out
        // Walker: 3.5-4.5 constant speed, incline varies (base 1-4%, push 6-8%, all-out 10-15%)
        ExerciseCoachingTarget("tread_base_pace", joggerSpeedMph = 5.0, runnerSpeedMph = 6.0, walkerSpeedMph = 3.5, walkerIncline = 4),
        ExerciseCoachingTarget("tread_push_pace", joggerSpeedMph = 6.0, runnerSpeedMph = 7.5, walkerSpeedMph = 3.5, walkerIncline = 8),
        ExerciseCoachingTarget("tread_all_out", joggerSpeedMph = 7.5, runnerSpeedMph = 9.0, walkerSpeedMph = 4.0, walkerIncline = 12),
        ExerciseCoachingTarget("tread_power_walk", walkerSpeedMph = 3.5, walkerIncline = 4),
        ExerciseCoachingTarget("tread_incline", walkerSpeedMph = 3.5, walkerIncline = 10),

        // Row — watt targets as minimum suggested
        // Beginner: 80-120 sustained, Intermediate: 120-200, Advanced: 200-300+
        ExerciseCoachingTarget("row_steady", rowWatts = 100),
        ExerciseCoachingTarget("row_power", rowWatts = 150, formCue = "Drive with your legs, strong pulls"),
        ExerciseCoachingTarget("row_all_out", rowWatts = 200, formCue = "Empty the tank"),

        // Floor — specific, actionable form cues
        ExerciseCoachingTarget("floor_squats", formCue = "Chest up, drive through your heels"),
        ExerciseCoachingTarget("floor_lunges", formCue = "Front knee over ankle, control the drop"),
        ExerciseCoachingTarget("floor_push_ups", formCue = "Core tight, full range of motion"),
        ExerciseCoachingTarget("floor_plank", formCue = "Shoulders over wrists, squeeze your core"),
        ExerciseCoachingTarget("floor_deadlifts", formCue = "Hinge at the hips, flat back"),
        ExerciseCoachingTarget("floor_chest_press", formCue = "Slow on the way down, squeeze at the top"),
        ExerciseCoachingTarget("floor_shoulder_press", formCue = "Core engaged, press straight overhead"),
        ExerciseCoachingTarget("floor_bicep_curls", formCue = "Control the tempo, squeeze at the top"),
        ExerciseCoachingTarget("floor_tricep_ext", formCue = "Elbows stay still, full extension"),
        ExerciseCoachingTarget("floor_trx_rows", formCue = "Sit tall, pull to your chest"),
        ExerciseCoachingTarget("floor_bench_hops", formCue = "Light on your feet, stay low"),
        ExerciseCoachingTarget("floor_pop_squats", formCue = "Explode up, land soft")
    ).associateBy { it.exerciseId }

    fun getTarget(exerciseId: String): ExerciseCoachingTarget? = targets[exerciseId]

    /**
     * Build a target clip key for the given exercise and tread mode.
     * Returns null if no target is applicable.
     */
    fun getTargetClipKey(exerciseId: String, treadMode: TreadMode, station: ExerciseStation): String? {
        val target = targets[exerciseId] ?: return null
        return when (station) {
            ExerciseStation.TREAD -> {
                when (treadMode) {
                    TreadMode.RUNNER -> {
                        val mph = target.runnerSpeedMph ?: target.joggerSpeedMph ?: return null
                        "target_${mph.toInt()}_mph"
                    }
                    TreadMode.POWER_WALKER -> target.walkerIncline?.let { "target_incline_$it" }
                }
            }
            ExerciseStation.ROW -> target.rowWatts?.let { "target_${it}_watts" }
            ExerciseStation.FLOOR -> null
        }
    }

    /**
     * Build a target TTS fallback string.
     */
    fun getTargetText(exerciseId: String, treadMode: TreadMode, station: ExerciseStation): String? {
        val target = targets[exerciseId] ?: return null
        return when (station) {
            ExerciseStation.TREAD -> {
                when (treadMode) {
                    TreadMode.RUNNER -> {
                        val mph = target.runnerSpeedMph ?: target.joggerSpeedMph ?: return null
                        "minimum ${mph.toInt()} miles per hour"
                    }
                    TreadMode.POWER_WALKER -> target.walkerIncline?.let { "incline to $it percent" }
                }
            }
            ExerciseStation.ROW -> target.rowWatts?.let { "$it watts" }
            ExerciseStation.FLOOR -> null
        }
    }

    /**
     * Get the form cue clip key for an exercise (floor or row with form cue).
     */
    fun getFormCueClipKey(exerciseId: String): String? {
        val target = targets[exerciseId] ?: return null
        if (target.formCue == null) return null
        return when {
            target.formCue.contains("heels", ignoreCase = true) -> "form_drive_heels"
            target.formCue.contains("chest up", ignoreCase = true) -> "form_chest_up"
            target.formCue.contains("full range", ignoreCase = true) -> "form_full_range"
            target.formCue.contains("squeeze", ignoreCase = true) -> "form_squeeze_top"
            target.formCue.contains("hinge", ignoreCase = true) -> "form_control_tempo"
            target.formCue.contains("breathe", ignoreCase = true) -> "form_breathe"
            else -> "form_control_tempo"
        }
    }

    /**
     * Get the push-harder clip key for mid-exercise encouragement.
     */
    fun getPushHarderClipKey(treadMode: TreadMode, station: ExerciseStation): String? {
        return when (station) {
            ExerciseStation.TREAD -> {
                when (treadMode) {
                    TreadMode.RUNNER -> "push_harder_add_1_mph"
                    TreadMode.POWER_WALKER -> "push_harder_raise_incline_2"
                }
            }
            ExerciseStation.ROW -> "push_harder_add_20_watts"
            ExerciseStation.FLOOR -> null
        }
    }

    /**
     * Get the push-harder TTS fallback text.
     */
    fun getPushHarderText(treadMode: TreadMode, station: ExerciseStation): String? {
        return when (station) {
            ExerciseStation.TREAD -> {
                when (treadMode) {
                    TreadMode.RUNNER -> "Push it, add 1 mile per hour!"
                    TreadMode.POWER_WALKER -> "Raise that incline 2 percent!"
                }
            }
            ExerciseStation.ROW -> "Add 20 watts, you've got this!"
            ExerciseStation.FLOOR -> null
        }
    }

    /**
     * Returns true if this exercise warrants mid-exercise push-harder cues
     * (push or all-out intensity exercises on tread or row).
     */
    fun isPushExercise(exerciseId: String): Boolean {
        return exerciseId in setOf(
            "tread_push_pace", "tread_all_out",
            "row_power", "row_all_out"
        )
    }

    fun isAllOutExercise(exerciseId: String): Boolean {
        return exerciseId in setOf("tread_all_out", "row_all_out")
    }
}
