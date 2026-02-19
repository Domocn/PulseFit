package com.example.pulsefit

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.pulsefit.data.model.NdProfile
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
import com.example.pulsefit.ui.onboarding.BleOnboardingScreen
import com.example.pulsefit.ui.onboarding.NdProfileSelectionScreen
import com.example.pulsefit.ui.onboarding.OnboardingSummaryScreen
import com.example.pulsefit.ui.onboarding.ProfileSetupScreen
import com.example.pulsefit.ui.onboarding.RestingHrScreen
import com.example.pulsefit.ui.onboarding.WelcomeScreen
import com.example.pulsefit.ui.progress.ProgressDashboardScreen
import com.example.pulsefit.ui.routine.RoutineBuilderScreen
import com.example.pulsefit.ui.settings.SensorySettingsScreen
import com.example.pulsefit.ui.settings.SettingsScreen
import com.example.pulsefit.ui.shop.RewardShopScreen
import com.example.pulsefit.ui.workout.PreWorkoutScheduleScreen
import com.example.pulsefit.ui.workout.ShutdownRoutineScreen
import com.example.pulsefit.ui.workout.SummaryScreen
import com.example.pulsefit.ui.workout.WorkoutScreen
import com.example.pulsefit.ui.workout.WorkoutTemplatesScreen
import com.example.pulsefit.ui.workout.WorkoutPhase

@Composable
fun PulseFitApp(viewModel: AppViewModel = hiltViewModel()) {
    val isOnboardingComplete by viewModel.isOnboardingComplete.collectAsState()
    val ndProfile by viewModel.ndProfile.collectAsState()
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
                                navController.navigate(Screen.BleOnboarding.route)
                            }
                        )
                    }
                    composable(Screen.BleOnboarding.route) {
                        BleOnboardingScreen(
                            onNext = {
                                navController.navigate(Screen.RestingHr.route)
                            }
                        )
                    }
                    composable(Screen.RestingHr.route) {
                        RestingHrScreen(
                            onNext = {
                                navController.navigate(Screen.OnboardingSummary.route)
                            }
                        )
                    }
                    composable(Screen.OnboardingSummary.route) {
                        OnboardingSummaryScreen(
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
                            },
                            onNavigateToTemplates = {
                                navController.navigate(Screen.WorkoutTemplates.route)
                            },
                            onNavigateToProgress = {
                                navController.navigate(Screen.ProgressDashboard.route)
                            },
                            onNavigateToShop = {
                                navController.navigate(Screen.RewardShop.route)
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
                                navController.navigate(Screen.ShutdownRoutine.createRoute(id)) {
                                    popUpTo(Screen.Home.route)
                                }
                            }
                        )
                    }
                    composable(
                        Screen.ShutdownRoutine.route,
                        arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
                        ShutdownRoutineScreen(
                            onComplete = {
                                navController.navigate(Screen.Summary.createRoute(workoutId)) {
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
                            },
                            onNavigateToDeepData = {
                                navController.navigate(Screen.DeepData.route)
                            },
                            onNavigateToTemplates = {
                                navController.navigate(Screen.WorkoutTemplates.route)
                            },
                            onNavigateToRewardShop = {
                                navController.navigate(Screen.RewardShop.route)
                            },
                            onNavigateToProgress = {
                                navController.navigate(Screen.ProgressDashboard.route)
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
                        WorkoutTemplatesScreen(
                            onSelectTemplate = { template ->
                                viewModel.createWorkoutFromTemplate(template.name) { workoutId ->
                                    // ASD/AUDHD: show phase preview for guided templates
                                    if ((ndProfile == NdProfile.ASD || ndProfile == NdProfile.AUDHD)
                                        && template.type == "GUIDED"
                                    ) {
                                        viewModel.setSelectedTemplate(template.name)
                                        navController.navigate(Screen.PreWorkoutSchedule.createRoute(workoutId))
                                    } else {
                                        navController.navigate(Screen.Workout.createRoute(workoutId))
                                    }
                                }
                            }
                        )
                    }
                    composable(
                        Screen.PreWorkoutSchedule.route,
                        arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
                        val templateName by viewModel.selectedTemplateName.collectAsState()
                        val phases = generatePhasesForTemplate(templateName ?: "Workout")
                        PreWorkoutScheduleScreen(
                            templateName = templateName ?: "Workout",
                            phases = phases,
                            onStart = {
                                viewModel.clearSelectedTemplate()
                                navController.navigate(Screen.Workout.createRoute(workoutId)) {
                                    popUpTo(Screen.Home.route)
                                }
                            }
                        )
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

private fun generatePhasesForTemplate(templateName: String): List<WorkoutPhase> {
    return when (templateName) {
        "Quick 15" -> listOf(
            WorkoutPhase("Warm-Up", 3, "Warm-Up"),
            WorkoutPhase("Main Set", 10, "Active"),
            WorkoutPhase("Cool Down", 2, "Rest")
        )
        "Steady State" -> listOf(
            WorkoutPhase("Warm-Up", 5, "Warm-Up"),
            WorkoutPhase("Steady Active", 20, "Active"),
            WorkoutPhase("Cool Down", 5, "Rest")
        )
        "HIIT Intervals" -> listOf(
            WorkoutPhase("Warm-Up", 3, "Warm-Up"),
            WorkoutPhase("Push Interval", 3, "Push"),
            WorkoutPhase("Recovery", 2, "Active"),
            WorkoutPhase("Push Interval", 3, "Push"),
            WorkoutPhase("Recovery", 2, "Active"),
            WorkoutPhase("Push Interval", 3, "Push"),
            WorkoutPhase("Cool Down", 4, "Rest")
        )
        "Endurance" -> listOf(
            WorkoutPhase("Warm-Up", 5, "Warm-Up"),
            WorkoutPhase("Build", 15, "Active"),
            WorkoutPhase("Sustain", 20, "Active"),
            WorkoutPhase("Cool Down", 5, "Rest")
        )
        else -> listOf(
            WorkoutPhase("Warm-Up", 5, "Warm-Up"),
            WorkoutPhase("Main", 20, "Active"),
            WorkoutPhase("Cool Down", 5, "Rest")
        )
    }
}
