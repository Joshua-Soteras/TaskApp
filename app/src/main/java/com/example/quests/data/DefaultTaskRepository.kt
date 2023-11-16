package com.example.quests.data

import com.example.quests.data.source.local.TaskDao
import com.example.quests.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

/**
 * Single entry point for managing tasks' data.
 *
 * @param localDataSource - The local data source
 * @param dispatcher - The dispatcher to be used for long running / complex operations such as
 * mapping many models.
 */
class DefaultTaskRepository @Inject constructor(
    private val localDataSource: TaskDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : TaskRepository {

    override fun getAllTasksStream(): Flow<List<Task>> {
        return localDataSource.getAllTasks().map { tasks ->
            withContext(dispatcher) {
                tasks.toExternal()
            }
        }
    }

    override fun getTaskStream(id: String): Flow<Task?> {
        return localDataSource.getTask(id).map {
            it.toExternal()
        }
    }

    override suspend fun insertTask(task: Task) = localDataSource.insert(task.toLocal())

    override suspend fun updateTask(task: Task) = localDataSource.update(task.toLocal())

    override suspend fun deleteTask(task: Task) = localDataSource.delete(task.toLocal())
}