package com.example.quests.data.source.network

import com.example.quests.data.source.network.model.NetworkTask

/**
 * Took this directly from the Android architecture sample
 * https://github.com/android/architecture-samples/blob/main/app/src/main/java/com/example/android/architecture/blueprints/todoapp/data/source/network/NetworkDataSource.kt
 */
interface NetworkDataSource {
    suspend fun loadTasks(): List<NetworkTask>

    suspend fun saveTasks(newTasks: List<NetworkTask>)
}