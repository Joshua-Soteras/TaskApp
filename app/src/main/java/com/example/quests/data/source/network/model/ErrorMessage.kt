package com.example.quests.data.source.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(
    val detail: String, // error message, could be nested, but we don't care here
    val status: String, // HTTP status code
    val title: String, // HTTP error response
)