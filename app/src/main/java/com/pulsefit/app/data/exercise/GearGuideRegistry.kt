package com.pulsefit.app.data.exercise

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GearGuideRegistry @Inject constructor() {

    data class GearItem(
        val id: String,
        val name: String,
        val description: String,
        val category: GearCategory,
        val sensoryNotes: String,
        val priceRange: String
    )

    enum class GearCategory(val label: String) {
        HEADPHONES("Headphones"),
        CLOTHING("Clothing"),
        ACCESSORIES("Accessories"),
        FOOTWEAR("Footwear"),
        RECOVERY_GEAR("Recovery Gear")
    }

    private val items = listOf(
        GearItem("gear_anc_headphones", "ANC Over-Ear Headphones", "Block gym noise completely with active noise cancellation",
            GearCategory.HEADPHONES, "Essential for noise sensitivity. Look for comfortable padding that won't irritate ears", "$$$"),
        GearItem("gear_bone_conduction", "Bone Conduction Headphones", "Hear music without blocking ears",
            GearCategory.HEADPHONES, "Good if in-ear buds cause discomfort. Allows spatial awareness", "$$"),
        GearItem("gear_loop_earplugs", "Loop Quiet Earplugs", "Reduce noise without music - just quiet",
            GearCategory.HEADPHONES, "Soft silicone. Reduces noise by 27dB without blocking everything", "$"),
        GearItem("gear_seamless_top", "Seamless Workout Top", "No seams or tags to irritate skin",
            GearCategory.CLOTHING, "Flat seams and tagless design. Soft moisture-wicking fabric", "$$"),
        GearItem("gear_compression_shorts", "Compression Shorts", "Consistent pressure can be calming",
            GearCategory.CLOTHING, "Provides proprioceptive input. Choose ones with wide, non-rolling waistband", "$$"),
        GearItem("gear_bamboo_socks", "Bamboo Workout Socks", "Ultra-soft, moisture-wicking, minimal seams",
            GearCategory.CLOTHING, "Bamboo fabric is softer than cotton. Look for seamless toe construction", "$"),
        GearItem("gear_loose_joggers", "Loose-Fit Joggers", "Freedom of movement without tight fabric",
            GearCategory.CLOTHING, "For those who find compression uncomfortable. Soft brushed interior", "$$"),
        GearItem("gear_sun_glasses", "Tinted Gym Glasses", "Reduce harsh fluorescent lighting",
            GearCategory.ACCESSORIES, "Yellow or rose tint reduces glare. Lightweight sports frames", "$$"),
        GearItem("gear_gym_towel", "Microfiber Gym Towel", "Soft, quick-dry, and compact",
            GearCategory.ACCESSORIES, "Smooth texture, not rough terry cloth. Good for wiping equipment", "$"),
        GearItem("gear_fidget_grip", "Grip Strength Fidget", "Something to squeeze between sets",
            GearCategory.ACCESSORIES, "Doubles as grip training. Satisfying tactile feedback", "$"),
        GearItem("gear_weighted_vest", "Light Weighted Vest (2-5 lb)", "Calming proprioceptive input during exercise",
            GearCategory.ACCESSORIES, "Like a weighted blanket for workouts. Distributes pressure evenly", "$$$"),
        GearItem("gear_wide_toe_shoes", "Wide Toe Box Trainers", "Room for toes to spread naturally",
            GearCategory.FOOTWEAR, "Reduces toe cramping discomfort. Look for minimal drop and flexible sole", "$$$"),
        GearItem("gear_cushioned_runners", "Max Cushion Running Shoes", "Extra cushioning absorbs impact",
            GearCategory.FOOTWEAR, "Reduces jarring impact that can be overwhelming. Soft, plush feel", "$$$"),
        GearItem("gear_slip_on_trainers", "Slip-On Training Shoes", "No laces to fuss with",
            GearCategory.FOOTWEAR, "Reduces pre-workout friction. Elastic/stretch entry", "$$"),
        GearItem("gear_foam_roller", "Textured Foam Roller", "Self-massage for recovery",
            GearCategory.RECOVERY_GEAR, "Choose smooth texture if bumps are uncomfortable. Firm but not hard", "$$"),
        GearItem("gear_massage_gun", "Percussion Massage Gun", "Deep tissue relief with adjustable intensity",
            GearCategory.RECOVERY_GEAR, "Start on lowest setting. Multiple head attachments for different sensitivities", "$$$"),
        GearItem("gear_yoga_mat", "Extra Thick Yoga Mat (6mm+)", "Cushion for floor exercises",
            GearCategory.RECOVERY_GEAR, "Thicker = more comfortable on hard floors. Non-slip surface", "$$"),
        GearItem("gear_resistance_bands", "Fabric Resistance Bands", "Soft fabric instead of rubber",
            GearCategory.RECOVERY_GEAR, "Won't snap or pinch like latex bands. Gentle on skin", "$"),
        GearItem("gear_cooling_towel", "Cooling Neck Towel", "Instant cooling sensation when wet",
            GearCategory.RECOVERY_GEAR, "Helps with temperature sensitivity during and after workouts", "$"),
        GearItem("gear_wrist_wrap", "Soft Wrist Wraps", "Wrist support with gentle compression",
            GearCategory.RECOVERY_GEAR, "Velcro-free options available for those sensitive to scratchy closures", "$")
    )

    fun getAll(): List<GearItem> = items

    fun getByCategory(category: GearCategory): List<GearItem> =
        items.filter { it.category == category }

    fun getById(id: String): GearItem? = items.find { it.id == id }
}
