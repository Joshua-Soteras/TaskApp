/*
 * Copyright 2022 The Android Open Source Project
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
 * - Combined RepositoryTestModule.kt and DatabaseTestModule.kt to one file
 * - Added function to provide binding to inject AuthRepository
 */

package com.example.quests.di

import android.content.Context
import androidx.room.Room
import com.example.quests.data.AuthRepository
import com.example.quests.data.FakeAuthRepository
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

    @Singleton
    @Provides
    fun provideAuthRepository() : AuthRepository {
        return FakeAuthRepository()
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