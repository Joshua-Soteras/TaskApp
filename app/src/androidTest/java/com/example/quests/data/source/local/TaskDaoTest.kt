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
 * Changes:
 * - Removed redundant annotations
 * - Use Kotest matchers
 */

package com.example.quests.data.source.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.filters.SmallTest
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
@SmallTest
class TaskDaoTest {
    private lateinit var taskDao: TaskDao
    private lateinit var database: QuestsDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            QuestsDatabase::class.java
        ).allowMainThreadQueries().build()
        taskDao = database.taskDao()
    }

    @After
    fun closeDb() = database.close()

    private var task1 = LocalTask("1", "Title", "desc")
    private var task2 = LocalTask("2", "2nd Title", "descdesc")

    private suspend fun addOneTaskToDb() {
        taskDao.insert(task1)
    }

    private suspend fun addTwoTasksToDb() {
        taskDao.insert(task1)
        taskDao.insert(task2)
    }

    @Test
    fun insertTaskAndGetTask() = runTest {
        // GIVEN - Insert a task
        addOneTaskToDb()

        // WHEN - Get the task by id from the database
        val loaded: LocalTask = taskDao.getTask(task1.id).first()

        // THEN - The loaded data contains the expected values
        loaded shouldNotBe null
        loaded.id shouldBe task1.id
        loaded.title shouldBe task1.title
        loaded.description shouldBe task1.description
    }

    @Test
    fun insertTasksAndGetTasks() = runTest {
        // GIVEN - Insert two tasks
        addTwoTasksToDb()

        // WHEN - Get all tasks
        val loaded: List<LocalTask> = taskDao.getAllTasks().first()

        // THEN - The loaded data contains the expected values
        loaded.size shouldBe 2
        for ((index, task) in loaded.withIndex()) {
            loaded[index].id shouldBe task.id
            loaded[index].title shouldBe task.title
            loaded[index].description shouldBe task.description
        }
    }

    @Test
    fun insertAllTasksAndGetTasks() = runTest {
        // GIVEN - a list of tasks
        val l = listOf(task1, task2)

        // WHEN - inserting the list and getting all tasks
        taskDao.insertAll(l)
        val loaded: List<LocalTask> = taskDao.getAllTasks().first()

        // THEN - the loaded data contains both tasks
        loaded.size shouldBe 2
        for ((index, task) in loaded.withIndex()) {
            loaded[index].id shouldBe task.id
            loaded[index].title shouldBe task.title
            loaded[index].description shouldBe task.description
        }
    }

    @Test
    fun updateTaskAndGetTask() = runTest {
        // GIVEN - Insert a task
        addOneTaskToDb()

        // WHEN - Updating a task and get task by id
        val newTask1 = LocalTask("1", "different", "different desc")
        taskDao.update(newTask1)
        val loaded: LocalTask = taskDao.getTask(task1.id).first()

        // THEN - The loaded data contains the expected values
        loaded.id shouldBe task1.id
        loaded.title shouldBe "different"
        loaded.description shouldBe "different desc"
    }

    @Test
    fun deleteTaskAndGetTask() = runTest {
        // GIVEN - Insert a task
        addOneTaskToDb()

        // WHEN - Deleting a task and get task by id
        taskDao.delete(task1)
        val loaded: LocalTask = taskDao.getTask(task1.id).first()

        // THEN - The loaded data is null
        loaded shouldBe null
    }

    @Test
    fun updateCompletionDateAndGetTask() = runTest {
        // GIVEN - insert a task
        addOneTaskToDb()

        // WHEN - updating the completion date
        val completionDate = 123L
        taskDao.updateCompletionDate(task1.id, completionDate)

        // THEN - loaded data has updated date
        val loaded = taskDao.getTask(task1.id).first()
        loaded.title shouldBe task1.title
        loaded.description shouldBe task1.description
        loaded.completionDate shouldBe completionDate
    }

    @Test
    fun deleteCompletedTasksAndGetAllTasks() = runTest {
        // GIVEN - a completed task and active task
        addOneTaskToDb()
        val completedTask = LocalTask(
            id = "test id", title = "title", description = "", completionDate = 1L
        )
        taskDao.insert(completedTask)

        // WHEN - deleting completed tasks
        taskDao.deleteCompletedTasks()

        // THEN - the completed task is not in database, but the active task is
        val loaded: List<LocalTask> = taskDao.getAllTasks().first()
        loaded shouldNotContain completedTask
        loaded shouldContain task1
    }
}