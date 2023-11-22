package com.example.quests.data

import com.example.quests.ui.util.getCurrentDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID

class FakeTaskRepository : TaskRepository {

    private val _savedTasks = MutableStateFlow(LinkedHashMap<String, Task>())
    val savedTasks: StateFlow<LinkedHashMap<String, Task>> = _savedTasks.asStateFlow()

    private val observableTasks: Flow<List<Task>> = savedTasks.map {
        it.values.toList()
    }

    override fun getAllTasksStream(): Flow<List<Task>> = observableTasks

    override fun getTaskStream(id: String): Flow<Task?> {
        return observableTasks.map { tasks ->
            // return from the map function
            return@map tasks.firstOrNull { it.id == id }
        }
    }

    override suspend fun createTask(title: String, description: String): String {
        val taskId = UUID.randomUUID().toString()
        val task = Task(
            id = taskId,
            title = title,
            description = description
        )
        insertTask(task)
        return taskId
    }

    override suspend fun insertTask(task: Task) {
        _savedTasks.update { tasks ->
            val newTasks = LinkedHashMap<String, Task>(tasks)
            newTasks[task.id] = task
            newTasks
        }
    }

    override suspend fun updateTask(task: Task) = insertTask(task)

    override suspend fun deleteTask(task: Task) {
        _savedTasks.update { tasks ->
            val newTasks = LinkedHashMap<String, Task>(tasks)
            newTasks.remove(task.id)
            newTasks
        }
    }

    override fun deleteAllTasks() {
        _savedTasks.update {
            LinkedHashMap()
        }
    }

    override suspend fun completeTask(id: String) {
        _savedTasks.value[id]?.let {
            insertTask(it.copy(completionDate = getCurrentDateTime().time))
        }
    }

    /**
     * For testing setup, easier way to insert multiple tasks.
     */
    fun addTasks(vararg tasks: Task) {
        _savedTasks.update { oldTasks ->
            val newTasks = LinkedHashMap<String, Task>(oldTasks)
            for (task in tasks) {
                newTasks[task.id] = task
            }
            newTasks
        }
    }
}