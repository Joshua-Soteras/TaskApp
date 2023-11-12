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

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTask(id: Int): Flow<LocalTask>

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<LocalTask>>
}