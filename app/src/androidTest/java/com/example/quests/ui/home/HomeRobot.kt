package com.example.quests.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.quests.HiltTestActivity
import com.example.quests.ui.task.AddTaskRobot

fun home(
    composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<HiltTestActivity>, HiltTestActivity>,
    func: HomeRobot.() -> Unit
) = HomeRobot(composeTestRule).apply { func() }

class HomeRobot(val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<HiltTestActivity>, HiltTestActivity>) {

    fun assertTitleIsDisplayed(title: String) {
        composeTestRule
            .onNodeWithText(title)
            .assertIsDisplayed()
    }

    fun assertNoTasksInDatabase() {
        composeTestRule
            .onNodeWithText("No tasks in the database.")
            .assertIsDisplayed()
    }

    fun assertHomeContentIsVisible() {
        composeTestRule
            .onNode(hasTestTag("HomeContent"))
            .assertIsDisplayed()
    }

    infix fun gotoAddTaskScreen(func: AddTaskRobot.() -> Unit): AddTaskRobot {
        composeTestRule
            .onNodeWithContentDescription("Create Task")
            .performClick()
        return AddTaskRobot(composeTestRule).apply { func() }
    }
}