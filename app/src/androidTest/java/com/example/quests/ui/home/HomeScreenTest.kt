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
                    viewModel = HomeViewModel(repository)
                )
            }
        }
    }

    @Test
    fun displayTask_whenRepositoryHasData() = runTest {
        // WHEN - there is a task in the repository
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
        // WHEN - there are no tasks in the repository
        repository.deleteAllTasks()

        // THEN - the no task message is displayed
        home(composeTestRule) {
            assertNoTasksInDatabase()
        }
    }

    // TODO: once we have filters set up for completed and (active + completed), we can do
    //  tests for marking tasks as complete
}