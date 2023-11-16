package com.example.quests.data

/**
 * Immutable model class for a Task.
 *
 * TODO: architecture sample says this constructor should be `internal`
 */
data class Task(
    val id: String,
    val title: String = "",
    val description: String = "",
)