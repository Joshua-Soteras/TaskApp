package com.example.quests.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quests.ui.home.HomeDestination
import com.example.quests.ui.home.HomeScreen
import com.example.quests.ui.task.AddTaskDestination
import com.example.quests.ui.task.AddTaskScreen

@Composable
fun QuestsNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                onAddTask = { navController.navigate(AddTaskDestination.route) }
            )
        }
        composable(route = AddTaskDestination.route) {
            AddTaskScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}