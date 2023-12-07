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
 * - Do not test for savedTasks.map() intentionally failing
 * - Removed functions that don't exist in our TaskRepository interface
 * - Different functions for completing tasks and handling network interactions
 */

package com.example.quests.data

import com.example.quests.util.toEpochMilli
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
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

    override suspend fun createTask(
        title: String,
        description: String,
        dueDate: Long,
    ): String {
        val taskId = UUID.randomUUID().toString()
        val task = Task(
            id = taskId,
            title = title,
            description = description,
            dueDate = dueDate
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
            insertTask(it.copy(completionDate = LocalDateTime.now().toEpochMilli()))
        }
    }

    override suspend fun activateTask(id: String) {
        _savedTasks.value[id]?.let {
            insertTask(it.copy(completionDate = 0L))
        }
    }

    override suspend fun clearCompletedTasks() {
        _savedTasks.update {
            // only keep the tasks which are not completed
            it.filterValues { task ->
                !task.isCompleted
            } as LinkedHashMap<String, Task>
        }
    }

    override suspend fun saveTasksToNetwork(onComplete: () -> Unit, onError: (String?) -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun loadTasksFromNetwork(onComplete: () -> Unit, onError: (String?) -> Unit) {
        TODO("Not yet implemented")
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