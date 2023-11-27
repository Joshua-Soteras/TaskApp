package com.example.quests.data

import kotlinx.coroutines.flow.Flow

class FakeAuthRepository : AuthRepository {
    override val authTokenFlow: Flow<AuthToken>
        get() = TODO("Not yet implemented")

    override suspend fun login(
        username: String,
        password: String,
        onComplete: () -> Unit,
        onError: (String?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun register(username: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun refresh(onComplete: () -> Unit, onError: (String?) -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun clearAuthToken() {
        TODO("Not yet implemented")
    }

    override suspend fun updateAccessToken(accessToken: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateRefreshToken(refreshToken: String) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchInitialAuthToken(): AuthToken {
        TODO("Not yet implemented")
    }
}