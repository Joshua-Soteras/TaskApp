package com.example.quests.data

import com.example.quests.data.source.local.TaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Single entry point for managing tasks' data.
 */
class DefaultTaskRepository @Inject constructor(
    private val localDataSource: TaskDao,
) : TaskRepository {

    override fun getAllTasksStream(): Flow<List<Task>> {
        return localDataSource.getAllTasks().map { tasks ->
            tasks.toExternal()
        }
    }

    override fun getTaskStream(id: Int): Flow<Task?> {
        return localDataSource.getTask(id).map {
            it.toExternal()
        }
    }

    override suspend fun insertTask(task: Task) = localDataSource.insert(task.toLocal())

    override suspend fun updateTask(task: Task) = localDataSource.update(task.toLocal())

    override suspend fun deleteTask(task: Task) = localDataSource.delete(task.toLocal())
}