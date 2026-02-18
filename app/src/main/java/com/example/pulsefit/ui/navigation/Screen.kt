package com.example.pulsefit.ui.navigation

sealed class Screen(val route: String) {
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
}
