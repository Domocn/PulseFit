package com.pulsefit.app.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object SignUp : Screen("sign_up")
    data object Welcome : Screen("welcome")
    data object ProfileSetup : Screen("profile_setup")
    data object NdProfileSelection : Screen("nd_profile_selection")
    data object Home : Screen("home")
    data object Workout : Screen("workout/{workoutId}") {
        fun createRoute(workoutId: Long) = "workout/$workoutId"
    }
    data object Summary : Screen("summary/{workoutId}") {
        fun createRoute(workoutId: Long) = "summary/$workoutId"
    }
    data object History : Screen("history")
    data object Settings : Screen("settings")
    data object SensorySettings : Screen("sensory_settings")
    data object Achievements : Screen("achievements")
    data object RoutineBuilder : Screen("routine_builder")
    data object WorkoutTemplates : Screen("workout_templates")
    data object DeepData : Screen("deep_data")
    data object RewardShop : Screen("reward_shop")
    data object ProgressDashboard : Screen("progress_dashboard")
    data object ShutdownRoutine : Screen("shutdown_routine/{workoutId}") {
        fun createRoute(workoutId: Long) = "shutdown_routine/$workoutId"
    }
    data object PreWorkoutSchedule : Screen("pre_workout_schedule/{workoutId}") {
        fun createRoute(workoutId: Long) = "pre_workout_schedule/$workoutId"
    }
    data object BleOnboarding : Screen("ble_onboarding")
    data object RestingHr : Screen("resting_hr")
    data object OnboardingSummary : Screen("onboarding_summary")

    // Social
    data object SocialHub : Screen("social_hub")
    data object Friends : Screen("friends")
    data object AddFriend : Screen("add_friend")
    data object Leaderboard : Screen("leaderboard")
    data object Feed : Screen("feed")
}
