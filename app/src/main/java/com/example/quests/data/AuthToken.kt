package com.example.quests.data

/**
 * Immutable model class to store access and refresh tokens.
 */
data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
) {
    fun isEmpty(): Boolean = accessToken.isEmpty() || refreshToken.isEmpty()
}
