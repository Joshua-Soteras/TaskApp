package com.example.quests.data.source.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    @SerialName(value = "access_token")
    val accessToken: String? = null,
    @SerialName(value = "refresh_token")
    val refreshToken: String? = null,
    val msg: String? = null, // JWT messages, missing headers, expired tokens, etc.
    val data: List<NetworkTask>? = null, // Whatever is stored at /data endpoint
    val error: ErrorMessage? = null,
)

@Serializable
data class ErrorMessage(
    val detail: String, // error message, could be nested, but we don't care here
    val status: String, // HTTP status code
    val title: String, // HTTP error response
)

@Serializable
data class User(
    val username: String,
    val password: String
)
