package com.pulsefit.app.data.exercise

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecoveryContentRegistry @Inject constructor() {

    data class RecoveryItem(
        val id: String,
        val title: String,
        val description: String,
        val category: RecoveryCategory,
        val durationMinutes: Int,
        val steps: List<String>
    )

    enum class RecoveryCategory(val label: String) {
        MOBILITY("Mobility"),
        FOAM_ROLL("Foam Rolling"),
        STRETCH("Stretching"),
        BREATHING("Breathing")
    }

    private val items = listOf(
        RecoveryItem("mob_hip_circles", "Hip Circles", "Gentle hip mobility to release tension",
            RecoveryCategory.MOBILITY, 3, listOf("Stand with feet hip-width apart", "Circle hips slowly clockwise 10x", "Reverse direction 10x", "Repeat with wider circles")),
        RecoveryItem("mob_cat_cow", "Cat-Cow Flow", "Spine mobility and breath work",
            RecoveryCategory.MOBILITY, 3, listOf("Start on all fours", "Inhale: drop belly, lift head (cow)", "Exhale: round spine, tuck chin (cat)", "Flow between for 2 minutes")),
        RecoveryItem("mob_shoulder_rolls", "Shoulder Rolls", "Release upper body tension",
            RecoveryCategory.MOBILITY, 2, listOf("Roll shoulders forward 10x", "Roll shoulders backward 10x", "Shrug and hold 5 seconds, release", "Repeat 3 times")),
        RecoveryItem("foam_quads", "Quad Roll", "Release tight quadriceps",
            RecoveryCategory.FOAM_ROLL, 4, listOf("Lie face down with roller under thighs", "Roll from hip to just above knee", "Pause on tender spots for 20 seconds", "Roll each leg for 2 minutes")),
        RecoveryItem("foam_it_band", "IT Band Roll", "Release outer thigh tightness",
            RecoveryCategory.FOAM_ROLL, 4, listOf("Lie on side with roller under outer thigh", "Roll from hip to just above knee", "Keep movements slow and controlled", "Spend extra time on tender areas")),
        RecoveryItem("foam_upper_back", "Upper Back Roll", "Release thoracic spine tension",
            RecoveryCategory.FOAM_ROLL, 3, listOf("Place roller under upper back", "Cross arms over chest", "Roll from mid-back to shoulders", "Pause on tight spots")),
        RecoveryItem("stretch_hamstrings", "Hamstring Stretch", "Seated forward fold for hamstrings",
            RecoveryCategory.STRETCH, 3, listOf("Sit with legs extended", "Hinge at hips reaching toward toes", "Hold 30 seconds, breathe deeply", "Repeat 3 times")),
        RecoveryItem("stretch_hip_flexor", "Hip Flexor Stretch", "Kneeling lunge stretch",
            RecoveryCategory.STRETCH, 3, listOf("Kneel in a lunge position", "Push hips gently forward", "Hold 30 seconds each side", "Keep torso upright")),
        RecoveryItem("stretch_chest", "Chest Opener", "Doorway chest stretch",
            RecoveryCategory.STRETCH, 2, listOf("Stand in a doorway, arms on frame", "Step forward gently", "Hold 30 seconds", "Repeat with arms higher and lower")),
        RecoveryItem("breathe_box", "Box Breathing", "4-4-4-4 calming breath pattern",
            RecoveryCategory.BREATHING, 5, listOf("Inhale for 4 counts", "Hold for 4 counts", "Exhale for 4 counts", "Hold for 4 counts", "Repeat for 5 minutes")),
        RecoveryItem("breathe_478", "4-7-8 Breathing", "Deep relaxation breathing technique",
            RecoveryCategory.BREATHING, 4, listOf("Inhale through nose for 4 counts", "Hold breath for 7 counts", "Exhale through mouth for 8 counts", "Repeat 4-6 cycles")),
        RecoveryItem("breathe_body_scan", "Body Scan", "Progressive relaxation from toes to head",
            RecoveryCategory.BREATHING, 5, listOf("Lie down comfortably", "Focus attention on toes, relax", "Slowly move attention up through body", "Spend 20-30 seconds on each area", "End with full-body awareness"))
    )

    fun getAll(): List<RecoveryItem> = items

    fun getByCategory(category: RecoveryCategory): List<RecoveryItem> =
        items.filter { it.category == category }

    fun getById(id: String): RecoveryItem? = items.find { it.id == id }
}
