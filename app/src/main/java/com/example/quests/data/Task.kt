package com.example.quests.data

import com.example.quests.util.toEpochMilli
import java.time.LocalDateTime

/**
 * Immutable model class for a Task.
 *
 * TODO: architecture sample says this constructor should be `internal`
 */
data class Task(
    val id: String,
    val title: String = "",
    val description: String = "",
    var completionDate: Long = 0L,
    var dueDate: Long = 0L,
) {
    /**
     * If completionDate is set (not 0), then task is completed
     */
    val isCompleted
        get() = completionDate > 0

    /**
     * If dueDate is set (not 0), then task has a due date
     */
    val hasDueDate
        get() = dueDate > 0

    /**
     * Larger the values, the further away we are from epoch, so if we
     * are behind the local date time, then we are late
     */
    val isLate
        get() = dueDate < LocalDateTime.now().toEpochMilli()
}