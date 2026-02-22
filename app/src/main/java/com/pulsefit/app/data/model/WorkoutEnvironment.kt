package com.pulsefit.app.data.model

/**
 * Where the user works out.
 */
enum class WorkoutEnvironment(val label: String) {
    GYM("Gym"),
    HOME("Home"),
    OUTDOOR("Outdoor"),
    HOTEL("Hotel / Travel")
}

/**
 * Equipment available to the user.
 * Grouped by category for UI display.
 */
enum class Equipment(
    val label: String,
    val category: EquipmentCategory,
    val defaultForGym: Boolean = false,
    val defaultForHome: Boolean = false
) {
    // Cardio
    TREADMILL("Treadmill", EquipmentCategory.CARDIO, defaultForGym = true),
    ROWER("Rowing Machine", EquipmentCategory.CARDIO, defaultForGym = true),
    BIKE("Stationary Bike", EquipmentCategory.CARDIO, defaultForGym = true),
    ELLIPTICAL("Elliptical", EquipmentCategory.CARDIO, defaultForGym = true),

    // Free weights
    DUMBBELLS("Dumbbells", EquipmentCategory.FREE_WEIGHTS, defaultForGym = true, defaultForHome = true),
    BARBELL("Barbell + Plates", EquipmentCategory.FREE_WEIGHTS, defaultForGym = true),
    KETTLEBELL("Kettlebell", EquipmentCategory.FREE_WEIGHTS, defaultForGym = true),

    // Resistance
    RESISTANCE_BANDS("Resistance Bands", EquipmentCategory.RESISTANCE, defaultForHome = true),
    TRX("TRX / Suspension Trainer", EquipmentCategory.RESISTANCE, defaultForGym = true),
    CABLE_MACHINE("Cable Machine", EquipmentCategory.RESISTANCE, defaultForGym = true),

    // Bodyweight / Floor
    BENCH("Weight Bench", EquipmentCategory.FLOOR, defaultForGym = true),
    YOGA_MAT("Yoga Mat", EquipmentCategory.FLOOR, defaultForHome = true),
    PULL_UP_BAR("Pull-Up Bar", EquipmentCategory.FLOOR),
    AB_ROLLER("Ab Roller", EquipmentCategory.FLOOR),
    BOSU_BALL("Bosu Ball", EquipmentCategory.FLOOR, defaultForGym = true),

    // Body only (always available)
    BODYWEIGHT("Bodyweight Only", EquipmentCategory.BODYWEIGHT, defaultForGym = true, defaultForHome = true)
}

enum class EquipmentCategory(val label: String) {
    CARDIO("Cardio Machines"),
    FREE_WEIGHTS("Free Weights"),
    RESISTANCE("Resistance"),
    FLOOR("Floor / Accessories"),
    BODYWEIGHT("Bodyweight")
}

/**
 * User's equipment profile, stored alongside their UserProfile.
 * Serialized as JSON in Room/Firestore.
 */
data class EquipmentProfile(
    val environment: WorkoutEnvironment = WorkoutEnvironment.GYM,
    val availableEquipment: Set<Equipment> = Equipment.entries
        .filter { it.defaultForGym }
        .toSet(),
    val preferredWorkoutDays: Set<DayOfWeek> = setOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
    ),
    val workoutsPerWeek: Int = 4,
    val preferredDurationMinutes: Int = 30
)

enum class DayOfWeek(val label: String, val short: String) {
    MONDAY("Monday", "Mon"),
    TUESDAY("Tuesday", "Tue"),
    WEDNESDAY("Wednesday", "Wed"),
    THURSDAY("Thursday", "Thu"),
    FRIDAY("Friday", "Fri"),
    SATURDAY("Saturday", "Sat"),
    SUNDAY("Sunday", "Sun")
}
