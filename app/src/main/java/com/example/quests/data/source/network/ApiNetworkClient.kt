package com.example.quests.data.source.network

import com.example.quests.data.source.network.model.NetworkTask
import com.example.quests.data.source.network.model.QuestsRequest
import com.example.quests.data.source.network.model.QuestsResponse
import com.example.quests.data.source.network.model.User
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ApiNetworkClient @Inject constructor(
    private val apiService: ApiService
) : ApiClient {

    // A mutex is used to ensure that reads and writes are thread-safe.
    private val accessMutex = Mutex()
    private var tasks = listOf<NetworkTask>()

    override suspend fun login(username: String, password: String): ApiResponse<QuestsResponse> =
        apiService.login(User(username, password))

    override suspend fun refresh(refreshToken: String): ApiResponse<QuestsResponse> =
        apiService.refresh("Bearer ".plus(refreshToken))

    override suspend fun saveTasks(
        accessToken: String,
        newTasks: List<NetworkTask>
    ): ApiResponse<QuestsResponse> =
        apiService.saveData(
            bearerAuth = "Bearer ".plus(accessToken),
            data = QuestsRequest(Json.encodeToString(newTasks))
        )

    override suspend fun loadTasks(): List<NetworkTask> {
        // TODO: Fetch from server, convert from JSON, return list to be save locally
        return tasks
    }
}