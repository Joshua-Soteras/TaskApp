package com.example.quests.data

import com.example.quests.data.source.FakeTaskDao
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultTaskRepositoryTest {
    private val task1 = Task(id = "1", title = "Title1", description = "Description1")
    private val task2 = Task(id = "2", title = "Title2", description = "Description2")

    private val localTasks = listOf(task1, task2).toLocal()

    // Test dependencies
    private lateinit var localDataSource: FakeTaskDao

    /*
     * TODO: (tentative) the Kotlin testing codelab recommends using Dispatcher.Main instead
     *  of Unconfined. It mentions that Dispatcher.Unconfined loses out on benefits such as being
     *  able to pause execution. Is that important enough to switch use Main?
     *  Our testDispatcher is also UnconfinedTestDispatcher not Dispatcher.Unconfined, so it
     *  skips delays.
     */
    private var testDispatcher = UnconfinedTestDispatcher()
    private var testScope = TestScope(testDispatcher)

    // Class under test
    private lateinit var taskRepository: DefaultTaskRepository

    @Before
    fun createRepository() {
        localDataSource = FakeTaskDao(localTasks)
        // Get a reference to the class under test
        taskRepository = DefaultTaskRepository(
            localDataSource = localDataSource,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun getAllTasksStream_containsTasksFromLocalDataSource() = testScope.runTest {
        // When tasks are requested from the tasks repository
        val tasks = taskRepository.getAllTasksStream().first()

        // All tasks from the local source are retrieved
        tasks shouldContainExactlyInAnyOrder localTasks.toExternal()
    }

    @Test
    fun getTaskStream_containsTaskFromLocalDataSource() = testScope.runTest {
        // GIVEN - task1 is in the local data source

        // THEN - getTaskStream() should get us task1
        val task = taskRepository.getTaskStream(task1.id).first()
        task shouldBe task1
    }

    @Test
    fun createTask_savesToLocal() = testScope.runTest {
        // WHEN - a new task is created
        val newTaskId = taskRepository.createTask("new task", "new description")

        // THEN - the local data source has the new task
        val retrievedTaskId = taskRepository.getTaskStream(newTaskId).first()?.id
        retrievedTaskId shouldBe newTaskId
    }

    @Test
    fun insertTask_savesToLocal() = testScope.runTest {
        // GIVEN - a new task
        val newTask = Task(id = "3", "title3", "desc3")

        // WHEN - inserting the new task
        taskRepository.insertTask(newTask)

        // THEN - the local data source contains the new task
        val retrievedTask = taskRepository.getTaskStream(newTask.id).first()
        newTask shouldBe retrievedTask
    }

    @Test
    fun updateTask() = testScope.runTest {
        // GIVEN - create a new task
        val oldTitle = "test title"
        val oldDescription = "test desc"
        val taskId = taskRepository.createTask(oldTitle, oldDescription)

        // WHEN - updating the task's fields
        val newTitle = oldTitle + "abc"
        val newDescription = oldDescription + "abc"
        val updatedTask = Task(taskId, newTitle, newDescription)
        taskRepository.updateTask(updatedTask)
        val task = taskRepository.getTaskStream(taskId).first()

        // THEN - the task's fields have changed
        task?.id shouldBe taskId
        task?.title shouldNotBe oldTitle
        task?.title shouldBe newTitle
        task?.description shouldNotBe oldDescription
        task?.description shouldBe newDescription
    }

    @Test
    fun deleteTask() = testScope.runTest {
        // GIVEN - create a new task
        val testTitle = "test title"
        val testDescription = "test desc"
        val taskId = taskRepository.createTask(testTitle, testDescription)
        val task = taskRepository.getTaskStream(taskId).first()

        // WHEN - deleting the task
        if (task != null) {
            taskRepository.deleteTask(task)
        }
        val tasks = taskRepository.getAllTasksStream().first()

        // THEN - the task is no longer in the data source
        tasks shouldNotContain task
    }

    @Test
    fun completeTask() = testScope.runTest {
        // GIVEN - a new task
        val testTitle = "a title"
        val taskId = taskRepository.createTask(testTitle)

        // WHEN - completing the task
        taskRepository.completeTask(taskId)
        val task = taskRepository.getTaskStream(taskId).first()

        // THEN - the task should be completed
        task?.isCompleted shouldBe true
    }
}