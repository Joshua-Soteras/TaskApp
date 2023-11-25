package com.example.quests.data.source.network

import com.skydoves.sandwich.ApiResponse
import javax.inject.Inject

interface AuthDataSource {

    // TODO: this should probably return something
    suspend fun login(username: String, password: String)
}