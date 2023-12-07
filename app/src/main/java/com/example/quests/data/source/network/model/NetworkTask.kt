package com.example.quests.data.source.network.model

import kotlinx.serialization.Serializable

/**
 * Internal model for the results of converting JSON to a Kotlin class. This is what
 * we convert to JSON to send to the backend.
 *
 * See ModelMappingExt.kt for mapping functions used to convert this model to other
 * models.
 */
@Serializable
data class NetworkTask(
    val id: String,
    val title: String = "",
    val description: String = "",
    var completionDate: Long = 0L,
    var dueDate: Long = 0L
)