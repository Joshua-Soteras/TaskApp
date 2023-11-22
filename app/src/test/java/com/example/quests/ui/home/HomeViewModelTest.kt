package com.example.quests.ui.home

import com.example.quests.MainCoroutineRule
import com.example.quests.data.FakeTaskRepository
import com.example.quests.data.Task
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    // Subject under test
    private lateinit var homeViewModel: HomeViewModel

    // Fake repository to be injected into the ViewModel
    private lateinit var taskRepository: FakeTaskRepository

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        taskRepository = FakeTaskRepository()
        // Create 3 tasks, 1 active, 2 completed
        val task1 = Task(id = "1", title = "Task 1", description = "Desc 1")
        val task2 = Task(id = "2", title = "Task 2", description = "Desc 2", completionDate = 1L)
        val task3 = Task(id = "3", title = "Task 3", description = "Desc 3", completionDate = 2L)
        taskRepository.addTasks(task1, task2, task3)

        homeViewModel = HomeViewModel(taskRepository)
    }

    @Test
    fun loadAllTAsksFromRepository() = runTest {
        homeViewModel.uiState.first().taskList.size shouldBe 3
    }

    @Test
    fun completeTask_dataAndSnackbarUpdated() = runTest {
        // GIVEN - create an active task in the repository
        val taskId = "test id"
        val task = Task(id = taskId, title = "test title")
        taskRepository.addTasks(task)

        // WHEN - completing the task
        homeViewModel.completeTask(task, true)

        // THEN - the task is completed, snackbarMessage and lastTaskCompleted are updated
        val uiState = homeViewModel.uiState.first()
        uiState.taskList.find { it.id == taskId }?.isCompleted shouldBe true
        uiState.snackbarMessage shouldNotBe null
        uiState.lastTaskCompleted?.id shouldBe taskId
    }
}