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
 * Changes:
 * - We handle the destination objects differently, instead following the
 *   example from the "Read and update data with Room" codelab
 *   https://developer.android.com/codelabs/basic-android-kotlin-compose-update-data-room?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-6-pathway-2%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-update-data-room#0
 * - Removed some navigation functions. We use navController.navigate()
 *   directly and only use functions here if we need to add options to
 *   the navigation
 */

package com.example.quests.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.quests.ui.backup.BackupDestination
import com.example.quests.ui.home.HomeDestination

/**
 * Interface to describe the navigation destinations for the app.
 * Copied approach from
 * https://github.com/google-developer-training/basic-android-kotlin-compose-training-inventory-app/blob/main/app/src/main/java/com/example/inventory/ui/navigation/NavigationDestination.kt
 */
interface NavigationDestination {
    /**
     * Unique name to define the path for a composable
     */
    val route: String

    /**
     * String resource id to that contains title to be displayed for the screen.
     */
    val titleRes: Int
}

/**
 * Functions for navigation actions.
 * Taken from architecture sample
 * https://github.com/android/architecture-samples/blob/main/app/src/main/java/com/example/android/architecture/blueprints/todoapp/TodoNavigation.kt
 */
class QuestsNavigationActions(private val navController: NavHostController) {

    fun navigateToHome(restoreStateValue: Boolean = true) {
        navController.navigate(HomeDestination.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = restoreStateValue
        }
    }

    fun navigateToBackup() {
        navController.navigate(BackupDestination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

