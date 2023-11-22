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

    @Update
    suspend fun update(task: LocalTask)

    @Delete
    suspend fun delete(task: LocalTask)

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTask(id: String): Flow<LocalTask>

    @Query("SELECT * FROM task")
    fun getAllTasks(): Flow<List<LocalTask>>

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
}