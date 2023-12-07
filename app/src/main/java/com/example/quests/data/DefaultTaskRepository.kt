package com.example.quests.data

import com.example.quests.data.source.local.TaskDao
import com.example.quests.data.source.network.ApiClient
import com.example.quests.data.source.network.model.NetworkTask
import com.example.quests.data.source.network.model.QuestsResponse
import com.example.quests.di.ApplicationScope
import com.example.quests.di.DefaultDispatcher
import com.example.quests.util.toEpochMilli
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import com.skydoves.sandwich.retrofit.serialization.deserializeErrorBody
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * Single entry point for managing tasks' data.
 *
 * @param localDataSource - The local data source
 * @param apiClient - Data source used to interact with backend
 * @param authRepository - Auth repository, used to refresh access tokens. #[ Ideally, we'd
 * create a repository that contains both task and auth repositories, but whatever. ]#
 * @param dispatcher - The dispatcher to be used for long running / complex operations such as
 * mapping many models.
 * @param scope - The coroutine scope used for deferred jobs where the result isn't important, such
 * as sending data to the network. #[ This is what the architecture sample says, I still don't
 * understand what's the problem of just using [dispatcher]. I guess the fact that they call
 * saveTasksToNetwork() after every operation, while we only want it during a specific button
 * press may be relevant. ]#
 */
class DefaultTaskRepository @Inject constructor(
    private val localDataSource: TaskDao,
    private val apiClient: ApiClient,
    private val authRepository: AuthRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
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

    /**
     * Creates a new task with the passed [title] and [description], returns the autogenerated UUID
     */
    override suspend fun createTask(
        title: String,
        description: String,
        dueDate: Long,
    ): String {
        val taskId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val task = Task(
            id = taskId,
            title = title,
            description = description,
            dueDate = dueDate
        )
        localDataSource.insert(task.toLocal())
        return taskId
    }

    override suspend fun insertTask(task: Task) = localDataSource.insert(task.toLocal())

    override suspend fun updateTask(task: Task) = localDataSource.update(task.toLocal())

    override suspend fun deleteTask(task: Task) = localDataSource.delete(task.toLocal())

    override fun deleteAllTasks() = localDataSource.deleteAllTasks()

    override suspend fun completeTask(id: String) {
        localDataSource.updateCompletionDate(id, LocalDateTime.now().toEpochMilli())
    }

    override suspend fun activateTask(id: String) {
        localDataSource.updateCompletionDate(id, 0L)
    }

    override suspend fun clearCompletedTasks() {
        localDataSource.deleteCompletedTasks()
    }

    /**
     * Uploads current list of local tasks to the network.
     */
    override suspend fun saveTasksToNetwork(onComplete: () -> Unit, onError: (String?) -> Unit) {
        scope.launch {
            authRepository.refresh({ }, onError)
            val accessToken: String = authRepository.fetchInitialAuthToken().accessToken
            // Return early so we don't waste time trying to upload tasks
            if (accessToken.isNullOrEmpty()) {
                return@launch
            }
            val localTasks = localDataSource.getAllTasksAsList()
            val networkTasks = withContext(dispatcher) {
                localTasks.toNetwork()
            }
            val response = apiClient.saveTasks(accessToken, networkTasks)
            response.onSuccess {
                onComplete()
            }.onError {
                val e: QuestsResponse? = this.deserializeErrorBody<String, QuestsResponse>()
                if (e?.msg != null) {
                    onError(e?.msg)
                } else if (e?.error?.detail != null) {
                    onError(e?.error?.detail)
                }
            }.onException {
                onError(message)
            }
        }
    }

    /**
     * Delete all tasks from local data source and replace them wih tasks from
     * the network.
     *
     * According to the architecture sample, we use `withContext` to handle the
     * case where the bulk `toLocal` mapping operation is complex.
     */
    override suspend fun loadTasksFromNetwork(onComplete: () -> Unit, onError: (String?) -> Unit) {
        withContext(dispatcher) {
            authRepository.refresh({ }, onError)
            val accessToken: String = authRepository.fetchInitialAuthToken().accessToken
            // Return early so we don't waste time trying to load tasks
            if (accessToken.isNullOrEmpty()) {
                return@withContext
            }
            val response = apiClient.getTasks(accessToken)
            response.suspendOnSuccess {
                val remoteTasks = data.data?.let {
                    try {
                        Json.decodeFromString<List<NetworkTask>>(it)
                    } catch (e: Exception) {
                        onError("Invalid data stored in server. Loading was aborted.")
                        null
                    }
                }
                if (remoteTasks != null) {
                    localDataSource.deleteAllTasks()
                    localDataSource.insertAll(remoteTasks.toLocal())
                    onComplete()
                }
            }.onError {
                val e: QuestsResponse? = this.deserializeErrorBody<String, QuestsResponse>()
                if (e?.msg != null) {
                    onError(e?.msg)
                } else if (e?.error?.detail != null) {
                    onError(e?.error?.detail)
                }
            }.onException {
                onError(message)
            }
        }
    }
}