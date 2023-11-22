package com.example.quests.ui.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.filters.LargeTest
import com.example.quests.HiltTestActivity
import com.example.quests.data.TaskRepository
import com.example.quests.ui.home.home
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@LargeTest
@HiltAndroidTest
class QuestsTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
    lateinit var navController: TestNavHostController

    @Inject
    lateinit var taskRepository: TaskRepository

    @Before
    fun init() {
        hiltRule.inject()

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            QuestsNavHost(navController)
        }
    }

    @Test
    fun createTask() = runTest {
        // GIVEN - an empty database
        taskRepository.deleteAllTasks()

        val taskTitle = "new create task title"
        home(composeTestRule) {
            assertHomeContentIsVisible()
            assertNoTasksInDatabase()
        } gotoAddTaskScreen {
            assertSaveIsNotEnabled()
            title(taskTitle)
            assertSaveIsEnabled()
        } save { }
        composeTestRule.awaitIdle()
        home(composeTestRule) {
            assertTitleIsDisplayed(taskTitle)
        }
    }

    @Test
    fun clearCompletedTasks() = runTest {
        // GIVEN - an active and completed task in the repository
        val completedTitle = "task to be completed"
        val activeTitle = "task that's still here"
        val completedTaskId = taskRepository.createTask(completedTitle)
        taskRepository.createTask(activeTitle)
        taskRepository.completeTask(completedTaskId)

        // WHEN - clear completed tasks is confirmed
        // THEN - the completed title does not exist and the active title does
        home(composeTestRule) {
            clearCompletedAndConfirm()
            // For some reason, even though the task has been deleted and it is visually gone,
            // the previous task item slots are still present, they're just invisible. The
            // data itself also thinks that the taskList is smaller.
            // You can see this most explicitly when the task list is longer than the screen, then
            // clearing the completed tasks, and notice that you can still scroll below.
            // So this is a crappy hack to force them to disappear.
            // But because I have to navigate between screens, this test is in the E2E file
            // rather than HomeScreenTest.kt
        } gotoAddTaskScreen { } backToHomeScreen {
            assertTitleDoesNotExist(completedTitle)
            assertTitleIsDisplayed(activeTitle)
        }
    }
}