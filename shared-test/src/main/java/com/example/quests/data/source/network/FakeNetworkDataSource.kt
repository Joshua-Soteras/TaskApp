package com.example.quests.data.source.network

// TODO: just took this from architecture sample, didn't look at it carefully yet

class FakeNetworkDataSource(
    var tasks: MutableList<NetworkTask>? = mutableListOf()
) : NetworkDataSource {
    override suspend fun loadTasks() = tasks ?: throw Exception("Task list is null")

    override suspend fun saveTasks(tasks: List<NetworkTask>) {
        this.tasks = tasks.toMutableList()
    }
}