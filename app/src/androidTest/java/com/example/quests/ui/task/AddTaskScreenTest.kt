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
}