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
    var completionDate: Long = 0L
) {
    /**
     * If completionDate is set (not 0), then task is completed
     */
    val isCompleted
        get() = completionDate > 0
}