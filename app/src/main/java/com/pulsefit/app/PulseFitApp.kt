package com.pulsefit.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.pulsefit.app.data.model.NdProfile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pulsefit.app.ui.achievements.AchievementsScreen
import com.pulsefit.app.ui.auth.LoginScreen
import com.pulsefit.app.ui.auth.SignUpScreen
import com.pulsefit.app.ui.components.PulseFitBottomBar
import com.pulsefit.app.ui.data.DeepDataScreen
import com.pulsefit.app.ui.history.HistoryScreen
import com.pulsefit.app.ui.home.HomeScreen
import com.pulsefit.app.ui.navigation.BottomNavItem
import com.pulsefit.app.ui.navigation.Screen
import com.pulsefit.app.ui.onboarding.BleOnboardingScreen
import com.pulsefit.app.ui.onboarding.NdProfileSelectionScreen
import com.pulsefit.app.ui.onboarding.OnboardingSummaryScreen
import com.pulsefit.app.ui.onboarding.ProfileSetupScreen
import com.pulsefit.app.ui.onboarding.RestingHrScreen
import com.pulsefit.app.ui.onboarding.WelcomeScreen
import com.pulsefit.app.ui.progress.ProgressDashboardScreen
import com.pulsefit.app.ui.routine.RoutineBuilderScreen
import com.pulsefit.app.ui.settings.SensorySettingsScreen
import com.pulsefit.app.ui.settings.SettingsScreen
import com.pulsefit.app.ui.shop.RewardShopScreen
import com.pulsefit.app.ui.social.AddFriendScreen
import com.pulsefit.app.ui.social.FeedScreen
import com.pulsefit.app.ui.social.FriendsScreen
import com.pulsefit.app.ui.social.LeaderboardScreen
import com.pulsefit.app.ui.social.SocialHubScreen
import com.pulsefit.app.ui.workout.PreWorkoutScheduleScreen
import com.pulsefit.app.ui.workout.ShutdownRoutineScreen
import com.pulsefit.app.ui.workout.SummaryScreen
import com.pulsefit.app.ui.workout.WorkoutScreen
import com.pulsefit.app.ui.workout.WorkoutTemplatesScreen
import com.pulsefit.app.ui.workout.WorkoutPhase

@Composable
fun PulseFitApp(viewModel: AppViewModel = hiltViewModel()) {
    val isOnboardingComplete by viewModel.isOnboardingComplete.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val ndProfile by viewModel.ndProfile.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in BottomNavItem.items.map { it.route }

    // Wait for both auth and onboarding state to resolve
    when {
        isAuthenticated == null || isOnboardingComplete == null -> { /* Loading */ }
        else -> {
            val startDestination = when {
                isAuthenticated != true -> Screen.Login.route
                isOnboardingComplete != true -> Screen.Welcome.route
                else -> Screen.Home.route
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
                    // Auth screens
                    composable(Screen.Login.route) {
                        LoginScreen(
                            onSignInSuccess = {
                                viewModel.refreshOnboardingState()
                                navController.navigate(
                                    if (isOnboardingComplete == true) Screen.Home.route
                                    else Screen.Welcome.route
                                ) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            onNavigateToSignUp = {
                                navController.navigate(Screen.SignUp.route)
                            }
                        )
                    }
                    composable(Screen.SignUp.route) {
                        SignUpScreen(
                            onSignUpSuccess = {
                                navController.navigate(Screen.Welcome.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // Onboarding screens
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

                    // Main screens
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

                    // Social screens
                    composable(Screen.SocialHub.route) {
                        SocialHubScreen(
                            onNavigateToFriends = {
                                navController.navigate(Screen.Friends.route)
                            },
                            onNavigateToLeaderboard = {
                                navController.navigate(Screen.Leaderboard.route)
                            },
                            onNavigateToFeed = {
                                navController.navigate(Screen.Feed.route)
                            }
                        )
                    }
                    composable(Screen.Friends.route) {
                        FriendsScreen(
                            onNavigateToAddFriend = {
                                navController.navigate(Screen.AddFriend.route)
                            }
                        )
                    }
                    composable(Screen.AddFriend.route) {
                        AddFriendScreen()
                    }
                    composable(Screen.Leaderboard.route) {
                        LeaderboardScreen()
                    }
                    composable(Screen.Feed.route) {
                        FeedScreen()
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
