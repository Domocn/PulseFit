package com.pulsefit.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(Screen.Home.route, "Home", Icons.Filled.FitnessCenter)
    data object Social : BottomNavItem(Screen.SocialHub.route, "Social", Icons.Filled.People)
    data object History : BottomNavItem(Screen.History.route, "History", Icons.Filled.History)
    data object Settings : BottomNavItem(Screen.Settings.route, "Settings", Icons.Filled.Settings)

    companion object {
        val items = listOf(Home, Social, History, Settings)
    }
}
