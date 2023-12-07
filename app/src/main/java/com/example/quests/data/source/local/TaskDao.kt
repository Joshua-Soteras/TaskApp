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
 * limitations under the License.
 *
 * Changes:
 * - Almost everything
 */

package com.example.quests.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the task table.
 */
@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: LocalTask)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(tasks: List<LocalTask>)

    @Update
    suspend fun update(task: LocalTask)

    @Delete
    suspend fun delete(task: LocalTask)

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTask(id: String): Flow<LocalTask>

    @Query("SELECT * FROM task")
    fun getAllTasks(): Flow<List<LocalTask>>

    @Query("SELECT * FROM task")
    fun getAllTasksAsList(): List<LocalTask>

    @Query("DELETE FROM task")
    fun deleteAllTasks()

    /**
     * Updates the completion date of a task
     *
     * @param id id of the task
     * @param completionDate date to set
     */
    @Query("UPDATE task SET completionDate = :completionDate WHERE id = :id")
    suspend fun updateCompletionDate(id: String, completionDate: Long)

    /**
     * Delete all tasks marked as completed (a non-zero completion date)
     */
    @Query("DELETE FROM task WHERE completionDate <> 0")
    suspend fun deleteCompletedTasks()
}