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
 * - Removed functions for getting List<Task> and Task?
 * - Changed DML functions to accept a Task rather than taskId (String)
 * - Added functions to load / save tasks to network because we handle
 *   the network data source differently
 */

package com.example.quests.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides CRUD operations for Tasks.
 */
interface TaskRepository {
    fun getAllTasksStream(): Flow<List<Task>>

    fun getTaskStream(id: String): Flow<Task?>

    suspend fun createTask(
        title: String,
        description: String = "",
        dueDate: Long = 0L
    ): String

    suspend fun insertTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    fun deleteAllTasks()

    suspend fun completeTask(id: String)

    suspend fun activateTask(id: String)

    suspend fun clearCompletedTasks()

    suspend fun saveTasksToNetwork(onComplete: () -> Unit, onError: (String?) -> Unit)

    suspend fun loadTasksFromNetwork(onComplete: () -> Unit, onError: (String?) -> Unit)
}