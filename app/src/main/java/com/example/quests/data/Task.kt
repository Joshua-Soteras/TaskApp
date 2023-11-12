package com.example.quests.data

/**
 * Immutable model class for a Task.
 */
data class Task(
    val id: Int,
    val title: String = "",
    val description: String = "",
)