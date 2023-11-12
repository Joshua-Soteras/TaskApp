package com.example.quests.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalTask::class], version = 1, exportSchema = false)
abstract class QuestsDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}