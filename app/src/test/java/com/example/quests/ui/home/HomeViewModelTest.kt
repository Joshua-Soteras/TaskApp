package com.example.quests.ui.home

import com.example.quests.MainCoroutineRule
import com.example.quests.data.FakeTaskRepository
import com.example.quests.data.Task
import io.kotest.matchers.shouldBe
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
        val task1 = Task(id = "1", title = "Task 1", description = "Desc 1")
        val task2 = Task(id = "2", title = "Task 2", description = "Desc 2")
        val task3 = Task(id = "3", title = "Task 3", description = "Desc 3")
        taskRepository.addTasks(task1, task2, task3)

        homeViewModel = HomeViewModel(taskRepository)
    }

    @Test
    fun loadAllTAsksFromRepository() = runTest {
        homeViewModel.uiState.first().taskList.size shouldBe 3
    }
}