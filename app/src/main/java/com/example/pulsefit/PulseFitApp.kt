package com.example.pulsefit

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pulsefit.ui.achievements.AchievementsScreen
import com.example.pulsefit.ui.components.PulseFitBottomBar
import com.example.pulsefit.ui.data.DeepDataScreen
import com.example.pulsefit.ui.history.HistoryScreen
import com.example.pulsefit.ui.home.HomeScreen
import com.example.pulsefit.ui.navigation.BottomNavItem
import com.example.pulsefit.ui.navigation.Screen
import com.example.pulsefit.ui.onboarding.NdProfileSelectionScreen
import com.example.pulsefit.ui.onboarding.ProfileSetupScreen
import com.example.pulsefit.ui.onboarding.WelcomeScreen
import com.example.pulsefit.ui.progress.ProgressDashboardScreen
import com.example.pulsefit.ui.routine.RoutineBuilderScreen
import com.example.pulsefit.ui.settings.SensorySettingsScreen
import com.example.pulsefit.ui.settings.SettingsScreen
import com.example.pulsefit.ui.shop.RewardShopScreen
import com.example.pulsefit.ui.workout.SummaryScreen
import com.example.pulsefit.ui.workout.WorkoutScreen
import com.example.pulsefit.ui.workout.WorkoutTemplatesScreen

@Composable
fun PulseFitApp(viewModel: AppViewModel = hiltViewModel()) {
    val isOnboardingComplete by viewModel.isOnboardingComplete.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in BottomNavItem.items.map { it.route }

    when (isOnboardingComplete) {
        null -> { /* Loading */ }
        else -> {
            val startDestination = if (isOnboardingComplete == true) {
                Screen.Home.route
            } else {
                Screen.Welcome.route
            }

            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        PulseFitBottomBar(navController)
                    }
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.padding(padding)
                ) {
                    composable(Screen.Welcome.route) {
                        WelcomeScreen(
                            onGetStarted = {
                                navController.navigate(Screen.ProfileSetup.route)
                            }
                        )
                    }
                    composable(Screen.ProfileSetup.route) {
                        ProfileSetupScreen(
                            onNext = {
                                navController.navigate(Screen.NdProfileSelection.route)
                            }
                        )
                    }
                    composable(Screen.NdProfileSelection.route) {
                        NdProfileSelectionScreen(
                            onComplete = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Welcome.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onStartWorkout = { workoutId ->
                                navController.navigate(Screen.Workout.createRoute(workoutId))
                            }
                        )
                    }
                    composable(
                        Screen.Workout.route,
                        arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
                        WorkoutScreen(
                            workoutId = workoutId,
                            onEnd = { id ->
                                navController.navigate(Screen.Summary.createRoute(id)) {
                                    popUpTo(Screen.Home.route)
                                }
                            }
                        )
                    }
                    composable(
                        Screen.Summary.route,
                        arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
                        SummaryScreen(
                            workoutId = workoutId,
                            onDone = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.History.route) {
                        HistoryScreen(
                            onWorkoutClick = { workoutId ->
                                navController.navigate(Screen.Summary.createRoute(workoutId))
                            }
                        )
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            onNavigateToSensory = {
                                navController.navigate(Screen.SensorySettings.route)
                            },
                            onNavigateToAchievements = {
                                navController.navigate(Screen.Achievements.route)
                            },
                            onNavigateToRoutineBuilder = {
                                navController.navigate(Screen.RoutineBuilder.route)
                            }
                        )
                    }
                    composable(Screen.SensorySettings.route) {
                        SensorySettingsScreen()
                    }
                    composable(Screen.Achievements.route) {
                        AchievementsScreen()
                    }
                    composable(Screen.RoutineBuilder.route) {
                        RoutineBuilderScreen()
                    }
                    composable(Screen.WorkoutTemplates.route) {
                        WorkoutTemplatesScreen()
                    }
                    composable(Screen.DeepData.route) {
                        DeepDataScreen()
                    }
                    composable(Screen.RewardShop.route) {
                        RewardShopScreen()
                    }
                    composable(Screen.ProgressDashboard.route) {
                        ProgressDashboardScreen()
                    }
                }
            }
        }
    }
}
