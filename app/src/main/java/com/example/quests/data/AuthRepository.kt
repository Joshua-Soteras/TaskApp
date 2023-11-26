package com.example.quests.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository for authentication functions for backend
 */
interface AuthRepository {

    val authTokenFlow: Flow<AuthToken>

    suspend fun login(
        username: String,
        password: String,
        onComplete: () -> Unit,
        onError: (String?) -> Unit
    )

    suspend fun register(username: String, password: String)

    suspend fun refresh()

    suspend fun clearAuthToken()
}