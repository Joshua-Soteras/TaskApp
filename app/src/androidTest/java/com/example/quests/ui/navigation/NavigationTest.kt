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
class NavigationTest {

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
    fun verifyStartDestination() = runTest {
        home(composeTestRule) {
            assertHomeContentIsVisible()
        }
    }

    @Test
    fun homeScreen_toAddTaskScreen_backToHomeScreen() = runTest {
        home(composeTestRule) {
            assertHomeContentIsVisible()
        } gotoAddTaskScreen {
            assertSaveIsNotEnabled()
        } backToHomeScreen {
            assertHomeContentIsVisible()
        }
    }
}