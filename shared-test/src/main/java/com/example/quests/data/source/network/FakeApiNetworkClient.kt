package com.example.quests.data.source.network

import com.example.quests.data.source.network.model.NetworkTask
import com.example.quests.data.source.network.model.QuestsResponse
import com.skydoves.sandwich.ApiResponse

class FakeApiNetworkClient : ApiClient {
    override suspend fun login(
        username: String,
        password: String
    ): ApiResponse<QuestsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun loadTasks(): List<NetworkTask> {
        TODO("Not yet implemented")
    }

    override suspend fun saveTasks(newTasks: List<NetworkTask>) {
        TODO("Not yet implemented")
    }
}