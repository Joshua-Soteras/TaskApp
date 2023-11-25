package com.example.quests.data.source.network

import com.skydoves.sandwich.ApiResponse
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun login(username: String, password: String) {
        val response: ApiResponse<Response> = apiService.login(User(username, password))
        println(response)
    }
}