/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Changes (from architecture sample's TodoNavGraph.kt):
 * - Use different routes
 * - Add fragile logic to handle communicating between screens
 */

package com.example.quests.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.quests.R
import com.example.quests.ui.backup.BackupDestination
import com.example.quests.ui.backup.BackupScreen
import com.example.quests.ui.backup.LoginDestination
import com.example.quests.ui.backup.LoginScreen
import com.example.quests.ui.backup.SignupDestination
import com.example.quests.ui.backup.SignupScreen
import com.example.quests.ui.home.HomeDestination
import com.example.quests.ui.home.HomeScreen
import com.example.quests.ui.task.AddTaskDestination
import com.example.quests.ui.task.AddTaskScreen
import com.example.quests.ui.task.TaskDetailDestination
import com.example.quests.ui.task.TaskDetailScreen
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
    var messageFromRegister: String? by remember { mutableStateOf(null) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            AppModalDrawer(drawerState, currentRoute, navActions) {
                HomeScreen(
                    onAddTask = { navController.navigate(AddTaskDestination.route) },
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                    onTaskClick = { task -> navController.navigate("task/${task.id}") }
                )
            }
        }
        composable(route = AddTaskDestination.route) {
            AddTaskScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(route = TaskDetailDestination.route) {
            TaskDetailScreen(
                navigateBack = { navController.popBackStack() },
                onDeleteTask = { navActions.navigateToHome(false) }
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
        // TODO: the logic to handle the message from sign up is very fragile, if we had
        //  other destinations we could arrive at from the signup page, you would put
        //  messageFromRegister = null there as well.
        composable(route = LoginDestination.route) {
            LoginScreen(
                navigateBack = { navController.popBackStack() },
                navigateToSignup = { navController.navigate(SignupDestination.route) },
                messageFromSignup = messageFromRegister,
            )
            messageFromRegister = null
        }
        composable(route = SignupDestination.route) {
            val s: String = stringResource(R.string.successfully_created_new_user)
            SignupScreen(
                navigateBack = { navController.popBackStack() },
                navigateBackOnSuccess = { navController.popBackStack(); messageFromRegister = s }
            )
        }
    }
}