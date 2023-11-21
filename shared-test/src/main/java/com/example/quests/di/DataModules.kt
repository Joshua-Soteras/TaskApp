package com.example.quests.di

import android.content.Context
import androidx.room.Room
import com.example.quests.data.FakeTaskRepository
import com.example.quests.data.TaskRepository
import com.example.quests.data.source.local.QuestsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

// https://developer.android.com/training/dependency-injection/hilt-testing

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object RepositoryTestModule {
    // TODO: why does the architecture sample use @Provides instead of @Binds here?
    @Singleton
    @Provides
    fun provideTaskRepository(): TaskRepository {
        return FakeTaskRepository()
    }
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object DatabaseTestModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): QuestsDatabase {
        return Room
            .inMemoryDatabaseBuilder(context.applicationContext, QuestsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }
}