/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *
 * Changes:
 * - Use completionDate instead of isCompleted for keeping track of completion
 * - Added dueDate
 * - Removed titleForList, isActive, and isEmpty properties
 */

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