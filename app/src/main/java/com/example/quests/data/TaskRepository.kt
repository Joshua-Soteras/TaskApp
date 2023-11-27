package com.example.quests.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides CRUD operations for Tasks.
 */
interface TaskRepository {
    fun getAllTasksStream(): Flow<List<Task>>

    fun getTaskStream(id: String): Flow<Task?>

    suspend fun createTask(title: String, description: String = ""): String

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