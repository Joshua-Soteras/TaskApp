/*
 * Copyright 2019 The Android Open Source Project
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
 * Changes (from architecture sample's AddEditTaskScreenTest.kt):
 * - Refactored UI interactions to use AddTaskRobot
 * - Only kept validTask_isSaved test. Same idea behind the test, but
 *   implemented differently
 */

package com.example.quests.ui.task

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.test.filters.MediumTest
import com.example.quests.HiltTestActivity
import com.example.quests.data.TaskRepository
import com.example.quests.ui.theme.QuestsTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
class AddTaskScreenTest {

    // https://developer.android.com/training/dependency-injection/hilt-testing#multiple-testrules
    // need (order = x) because of multiple rules

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var repository: TaskRepository

    @Before
    fun init() {
        hiltRule.inject()

        // GIVEN - on the "Add Task" screen
        composeTestRule.setContent {
            QuestsTheme {
                AddTaskScreen(
                    navigateBack = { },
                    viewModel = AddTaskViewModel(repository)
                )
            }
        }
    }

    @Test
    fun validTask_isCreated() = runTest {
        // GIVEN - an empty repository
        repository.deleteAllTasks()

        // WHEN - title and description is filled out
        val title = "a title"
        val description = "a desc"
        addTask(composeTestRule) {
            assertSaveIsNotEnabled() // also tests that Save is not enabled with empty title
            title(title)
            assertSaveIsEnabled()
            description(description)
        } save { }

        composeTestRule.awaitIdle()
        composeTestRule.onRoot().printToLog("TAG")

        // THEN - the task is saved in the repository
        val tasks = repository.getAllTasksStream().first()
        tasks.size shouldBe 1
        tasks[0].title shouldBe title
        tasks[0].description shouldBe description
    }
    @Test
    fun emptyTask_isNotCreated() = runTest {
        // GIVEN - an empty repository
        repository.deleteAllTasks()

        // WHEN - no title and description are filled out
        addTask(composeTestRule) {
            assertSaveIsNotEnabled()
        } save { }

        composeTestRule.awaitIdle()

        // THEN - no task should be saved in the repository
        val tasks = repository.getAllTasksStream().first()
        tasks.size shouldBe 0
    }
    @Test
    fun uiInteraction_saveButtonBehavior() = runTest {
        // GIVEN - an empty repository
        repository.deleteAllTasks()

        // WHEN - title and description are filled out
        addTask(composeTestRule) {
            assertSaveIsNotEnabled() // Save button should be initially disabled
            title("Test Title")
            description("Test Description")
            assertSaveIsEnabled() // Save button should be enabled now
        } save { }

        composeTestRule.awaitIdle()

        // THEN - the task should be saved in the repository
        val tasks = repository.getAllTasksStream().first()
        tasks.size shouldBe 1
        tasks[0].title shouldBe "Test Title"
        tasks[0].description shouldBe "Test Description"
    }
}