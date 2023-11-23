package com.example.quests.data.source.network

import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class TaskNetworkDataSource @Inject constructor() : NetworkDataSource {

    // A mutex is used to ensure that reads and writes are thread-safe.
    private val accessMutex = Mutex()
    private var tasks = listOf<NetworkTask>()

    override suspend fun loadTasks(): List<NetworkTask> {
        // TODO: Fetch from server, convert from JSON, return list to be save locally
        return tasks
    }
    override suspend fun saveTasks(newTasks: List<NetworkTask>) {
        // TODO: Convert to JSON and send to server
        val data = Json.encodeToString(newTasks)
        println(data)
        val x = Json.decodeFromString<List<NetworkTask>>(data)
        println(x)
        /**
         * [{"id":"7c594d23-59ae-4781-a37d-d5b6566d2fc4","title":"a"},{"id":"459c5f0d-50c9-40d1-861a-94d47a284f57","title":"a","description":"a","completionDate":1700728573616}]
         * [NetworkTask(id=7c594d23-59ae-4781-a37d-d5b6566d2fc4, title=a, description=, completionDate=0), NetworkTask(id=459c5f0d-50c9-40d1-861a-94d47a284f57, title=a, description=a, completionDate=1700728573616)]
         */
        tasks = newTasks
    }

}