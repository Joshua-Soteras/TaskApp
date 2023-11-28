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

    suspend fun register(
        username: String,
        password: String,
        onComplete: () -> Unit,
        onError: (String?) -> Unit
    )

    suspend fun refresh(onComplete: () -> Unit, onError: (String?) -> Unit)

    suspend fun clearAuthToken()

    suspend fun updateAccessToken(accessToken: String)

    suspend fun updateRefreshToken(refreshToken: String)

    suspend fun fetchInitialAuthToken(): AuthToken
}