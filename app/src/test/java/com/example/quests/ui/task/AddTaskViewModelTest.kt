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