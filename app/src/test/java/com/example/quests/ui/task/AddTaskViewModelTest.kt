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
 * Changes (form AddEditTaskViewModelTest.kt):
 * - Slightly modified boilerplate code
 * - Removed most tests, for the tests still here, modified to work with
 *   our view model because in differences of how our view models work
 */

package com.example.quests.ui.task

import com.example.quests.MainCoroutineRule
import com.example.quests.data.FakeTaskRepository
import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddTaskViewModelTest {

    // Subject under test
    private lateinit var addTaskViewModel: AddTaskViewModel

    // Fake repository to be injected into the ViewModel
    private lateinit var taskRepository: FakeTaskRepository

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        taskRepository = FakeTaskRepository()
    }

    @Test
    fun createTask_savesTaskToRepository() {
        // GIVEN - a ViewModel with a filled in title and description
        addTaskViewModel = AddTaskViewModel(taskRepository)
        val newTitle = "New title"
        val newDescription = "New desc"
        addTaskViewModel.apply {
            updateTitle(newTitle)
            updateDescription(newDescription)
        }

        // WHEN - calling createTask()
        addTaskViewModel.createTask()

        // THEN - the new task is in the repository
        val newTask = taskRepository.savedTasks.value.values.first()
        newTask.title shouldBe newTitle
        newTask.description shouldBe newDescription
    }
}