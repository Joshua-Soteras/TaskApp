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

    override suspend fun register(username: String, password: String): ApiResponse<Void> {
        TODO("Not yet implemented")
    }

    override suspend fun refresh(refreshToken: String): ApiResponse<QuestsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun saveTasks(
        accessToken: String,
        newTasks: List<NetworkTask>
    ): ApiResponse<QuestsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getTasks(accessToken: String): ApiResponse<QuestsResponse> {
        TODO("Not yet implemented")
    }
}