package com.pulsefit.app.util

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MicroWorkoutEngine @Inject constructor() {

    data class MicroWorkout(
        val id: String,
        val name: String,
        val description: String,
        val durationSeconds: Int,
        val steps: List<String>,
        val burnPoints: Int
    )

    private val workouts = listOf(
        MicroWorkout("micro_desk_stretch", "Desk Stretch", "Quick stretch for tight muscles", 120,
            listOf("Neck rolls (30s)", "Shoulder shrugs (30s)", "Seated twist (30s each side)"), 1),
        MicroWorkout("micro_standing_flow", "Standing Flow", "Get blood flowing without changing clothes", 150,
            listOf("March in place (30s)", "Arm circles (30s)", "Standing side bends (30s)", "Calf raises (30s)"), 1),
        MicroWorkout("micro_power_minute", "Power Minute", "One minute of focused movement", 60,
            listOf("10 jumping jacks", "10 squats", "10 arm swings", "Hold mountain pose 10s"), 1),
        MicroWorkout("micro_breath_move", "Breath & Move", "Combine breathing with gentle movement", 180,
            listOf("Deep breathing (30s)", "Gentle arm raises with breath (30s)", "Standing cat-cow (30s)", "Body shake-out (30s)", "Final deep breaths (30s)"), 1),
        MicroWorkout("micro_chair_workout", "Chair Workout", "Exercise without leaving your seat", 150,
            listOf("Seated marching (30s)", "Chair dips x10", "Seated knee lifts (30s)", "Ankle circles (30s)"), 1),
        MicroWorkout("micro_stair_blast", "Stair Blast", "Quick staircase cardio burst", 120,
            listOf("Walk up stairs briskly x3", "Step-ups on bottom step x10", "Calf raises on step x10"), 2),
        MicroWorkout("micro_wall_workout", "Wall Workout", "Use any wall for a quick session", 150,
            listOf("Wall push-ups x10", "Wall sit (30s)", "Wall angels x10", "Wall calf raises x10"), 1),
        MicroWorkout("micro_dance_break", "Dance Break", "Just move to music for 2 minutes", 120,
            listOf("Put on any song", "Move however feels good", "No rules, just move!"), 1),
        MicroWorkout("micro_balance_check", "Balance Check", "Quick proprioception work", 120,
            listOf("Single leg stand (30s each)", "Heel-to-toe walk (30s)", "Eyes-closed balance (10s each leg)"), 1),
        MicroWorkout("micro_energy_reset", "Energy Reset", "When you need a brain break", 150,
            listOf("Cross-body arm swings (20s)", "High knees (20s)", "Gentle jumping (20s)", "Deep breathing (30s)", "Shake it out (20s)"), 1)
    )

    fun getAll(): List<MicroWorkout> = workouts

    fun getRandom(): MicroWorkout = workouts.random()

    fun getById(id: String): MicroWorkout? = workouts.find { it.id == id }
}
