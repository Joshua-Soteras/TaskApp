package com.example.quests.data.source.network

import com.example.quests.data.source.network.model.NetworkTask
import com.example.quests.data.source.network.model.QuestsResponse
import com.skydoves.sandwich.ApiResponse

interface ApiClient {

    suspend fun login(username: String, password: String): ApiResponse<QuestsResponse>

    // TODO: make these return ApiResponse<>
    suspend fun saveTasks(
        accessToken: String,
        newTasks: List<NetworkTask>
    ): ApiResponse<QuestsResponse>

    suspend fun loadTasks(): List<NetworkTask>
}