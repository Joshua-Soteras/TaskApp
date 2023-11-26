package com.example.quests.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.quests.ui.backup.BackupDestination
import com.example.quests.ui.backup.BackupScreen
import com.example.quests.ui.backup.LoginDestination
import com.example.quests.ui.backup.LoginScreen
import com.example.quests.ui.home.HomeDestination
import com.example.quests.ui.home.HomeScreen
import com.example.quests.ui.task.AddTaskDestination
import com.example.quests.ui.task.AddTaskScreen
import com.example.quests.ui.util.AppModalDrawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun QuestsNavHost(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = HomeDestination.route,
    navActions: QuestsNavigationActions = remember(navController) {
        QuestsNavigationActions(navController)
    },
    modifier: Modifier = Modifier,
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            AppModalDrawer(drawerState, currentRoute, navActions) {
                HomeScreen(
                    onAddTask = { navController.navigate(AddTaskDestination.route) },
                    openDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }
        composable(route = AddTaskDestination.route) {
            AddTaskScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(route = BackupDestination.route) {
            AppModalDrawer(drawerState, currentRoute, navActions) {
                BackupScreen(
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                    navigateToLogin = { navController.navigate(LoginDestination.route) }
                )
            }
        }
        composable(route = LoginDestination.route) {
            LoginScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}