package com.example.quests.data.source.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestsResponse(
    @SerialName(value = "access_token")
    val accessToken: String? = null,
    @SerialName(value = "refresh_token")
    val refreshToken: String? = null,
    val msg: String? = null, // JWT messages, missing headers, expired tokens, etc.
    val data: List<NetworkTask>? = null, // Whatever is stored at /data endpoint
    val error: ErrorMessage? = null,
)
