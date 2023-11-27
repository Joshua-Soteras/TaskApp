package com.example.quests.data.source.network.model

import kotlinx.serialization.Serializable

@Serializable
data class QuestsRequest(
    val data: String? = null, // Whatever is stored at /data endpoint
)