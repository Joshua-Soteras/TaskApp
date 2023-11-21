package com.example.quests.data.source.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
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
    @Throws(IOException::class)
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
    @Throws(IOException::class)
    fun insertTaskAndGetById() = runTest {
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
    @Throws(IOException::class)
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
    @Throws(IOException::class)
    fun updateTaskAndGetById() = runTest {
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
    @Throws(IOException::class)
    fun deleteTaskAndGetById() = runTest {
        // GIVEN - Insert a task
        addOneTaskToDb()

        // WHEN - Deleting a task and get task by id
        taskDao.delete(task1)
        val loaded: LocalTask = taskDao.getTask(task1.id).first()

        // THEN - The loaded data is null
        loaded shouldBe null
    }
}