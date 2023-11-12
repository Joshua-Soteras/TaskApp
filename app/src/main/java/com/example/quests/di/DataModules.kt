package com.example.quests.di

import android.content.Context
import androidx.room.Room
import com.example.quests.data.DefaultTaskRepository
import com.example.quests.data.TaskRepository
import com.example.quests.data.source.local.QuestsDatabase
import com.example.quests.data.source.local.TaskDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTaskRepository(repository: DefaultTaskRepository): TaskRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): QuestsDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            QuestsDatabase::class.java,
            name = "Quests.db"
        ).build()
    }

    @Provides
    fun provideTaskDao(database: QuestsDatabase): TaskDao = database.taskDao()
}