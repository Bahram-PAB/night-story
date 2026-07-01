package com.nightstory.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Screen("home", "خانه", Icons.Default.Home)
    data object History : Screen("history", "تاریخچه", Icons.Default.History)
    data object Settings : Screen("settings", "تنظیمات", Icons.Default.Settings)
    data object About : Screen("about", "درباره", Icons.Default.Info)
}

val bottomNavItems = listOf(Screen.Home, Screen.History, Screen.Settings)