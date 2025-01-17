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
 * Changes made:
 * - Added bindAuthRepository() to inject the AuthRepository interface
 * - Removed bindNetworkDataSource() (we use the ApiClient as our network
 *   data source)
 * - Add module for Preferences DataStore
 * - Modified provideDatabase() to provide our database
 */

package com.example.quests.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.quests.data.AuthRepository
import com.example.quests.data.DefaultAuthRepository
import com.example.quests.data.DefaultTaskRepository
import com.example.quests.data.TaskRepository
import com.example.quests.data.source.local.QuestsDatabase
import com.example.quests.data.source.local.TaskDao
import com.example.quests.data.source.network.ApiClient
import com.example.quests.data.source.network.ApiNetworkClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val USER_PREFERENCES = "user_preferences"

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTaskRepository(repository: DefaultTaskRepository): TaskRepository

    @Singleton
    @Binds
    abstract fun bindAuthRepository(repository: DefaultAuthRepository): AuthRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindApiClient(dataSource: ApiNetworkClient): ApiClient
}

/**
 * Taken from
 * https://medium.com/androiddevelopers/datastore-and-dependency-injection-ea32b95704e3
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(appContext,USER_PREFERENCES)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }
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

    @Singleton
    @Provides
    fun provideTaskDao(database: QuestsDatabase): TaskDao = database.taskDao()
}