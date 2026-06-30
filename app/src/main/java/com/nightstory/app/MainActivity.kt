package com.nightstory.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nightstory.app.data.SettingsStore
import com.nightstory.app.ui.history.HistoryScreen
import com.nightstory.app.ui.home.HomeScreen
import com.nightstory.app.ui.navigation.Screen
import com.nightstory.app.ui.navigation.bottomNavItems
import com.nightstory.app.ui.settings.SettingsScreen
import com.nightstory.app.ui.strings.LocalStrings
import com.nightstory.app.ui.strings.LocalizationManager
import com.nightstory.app.ui.theme.NightStoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsStore = remember { SettingsStore(applicationContext) }
            val currentLanguage = remember { settingsStore.storyLanguage }
            val strings = remember(currentLanguage) { LocalizationManager.getStrings(currentLanguage) }
            val layoutDirection = remember(currentLanguage) {
                if (isRTL(currentLanguage)) LayoutDirection.Rtl else LayoutDirection.Ltr
            }

            NightStoryTheme {
                CompositionLocalProvider(
                    LocalStrings provides strings,
                    LocalLayoutDirection provides layoutDirection
                ) {
                    NightStoryNavHost()
                }
            }
        }
    }
}

fun isRTL(language: String): Boolean {
    return language in listOf("Arabic", "Persian", "Hebrew", "Urdu")
}

@Composable
fun NightStoryNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val s = LocalStrings.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    val label = when (screen) {
                        is Screen.Home -> s.navHome
                        is Screen.History -> s.navHistory
                        is Screen.Settings -> s.navSettings
                    }
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.History.route) { HistoryScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
