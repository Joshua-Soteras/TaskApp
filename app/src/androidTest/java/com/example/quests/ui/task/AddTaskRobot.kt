package com.example.quests.ui.task

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.quests.HiltTestActivity
import com.example.quests.ui.home.HomeRobot

fun addTask(
    composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<HiltTestActivity>, HiltTestActivity>,
    func: AddTaskRobot.() -> Unit
    // use .apply so addTask still returns the robot rather than void
) = AddTaskRobot(composeTestRule).apply { func() }

class AddTaskRobot(val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<HiltTestActivity>, HiltTestActivity>) {

    fun title(title: String) {
        composeTestRule
            .onNodeWithText("Title")
            .performTextInput(title)
    }

    fun description(description: String) {
        composeTestRule
            .onNodeWithText("Description")
            .performTextInput(description)
    }

    fun assertSaveIsNotEnabled() {
        composeTestRule
            .onNodeWithText("Save")
            .assertIsNotEnabled()
    }

    fun assertSaveIsEnabled() {
        composeTestRule
            .onNodeWithText("Save")
            .assertIsEnabled()
    }

    infix fun backToHomeScreen(func: HomeRobot.() -> Unit): HomeRobot {
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()
        return HomeRobot(composeTestRule).apply { func() }
    }

    infix fun save(func: HomeRobot.() -> Unit): HomeRobot {
        composeTestRule
            .onNodeWithText("Save")
            .performClick()
        return HomeRobot(composeTestRule).apply { func() }
    }
}