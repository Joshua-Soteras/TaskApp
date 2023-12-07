package com.example.quests.data.source.network.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val password: String
)