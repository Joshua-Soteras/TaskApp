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
 * Changes (from architecture sample's TasksScreenTests.kt):
 * - Refactored UI interactions to a HomeScreenRobot
 * - Removed most tests except displayTask_whenRepositoryHasData
 */

package com.example.quests.ui.home

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.filters.MediumTest
import com.example.quests.HiltTestActivity
import com.example.quests.data.TaskRepository
import com.example.quests.ui.theme.QuestsTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
class HomeScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var repository: TaskRepository

    @Before
    fun init() {
        hiltRule.inject()

        // GIVEN - on the home screen
        composeTestRule.setContent {
            QuestsTheme {
                HomeScreen(
                    onAddTask = { },
                    openDrawer = { },
                    viewModel = HomeViewModel(repository),
                    onTaskClick = { }
                )
            }
        }
    }

    @Test
    fun displayTask_whenRepositoryHasData() = runTest {
        // GIVEN - there is a task in the repository
        val title = "test display title"
        repository.createTask(title)

        composeTestRule.awaitIdle()

        // THEN - the task is displayed on the home screen
        home(composeTestRule) {
            assertTitleIsDisplayed(title)
        }
    }

    @Test
    fun displayEmptyMessage_whenRepositoryHasNoData() = runTest {
        // GIVEN - there are no tasks in the repository
        repository.deleteAllTasks()

        // THEN - the no task message is displayed
        home(composeTestRule) {
            assertNoTasksInDatabase()
        }
    }

    // TODO: once we have filters set up for completed and (active + completed), we can do
    //  tests for marking tasks as complete
}