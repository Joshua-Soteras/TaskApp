package com.example.quests.data.source

import com.example.quests.data.source.local.LocalTask
import com.example.quests.data.source.local.TaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTaskDao(initialTasks: List<LocalTask>? = emptyList()) : TaskDao {

    private var _tasks: MutableMap<String, LocalTask>? = null

    var tasks: List<LocalTask>?
        get() = _tasks?.values?.toList()
        set(newTasks) {
            _tasks = newTasks?.associateBy { it.id }?.toMutableMap()
        }

    init {
        tasks = initialTasks
    }

    override suspend fun insert(task: LocalTask) {
        _tasks?.put(task.id, task)
    }

    override suspend fun update(task: LocalTask) {
        _tasks?.put(task.id, task)
    }

    override suspend fun delete(task: LocalTask) {
        _tasks?.remove(task.id)
    }

    override fun getTask(id: String): Flow<LocalTask> = flow {
        _tasks?.get(id)?.let { emit(it) }
    }

    override fun getAllTasks(): Flow<List<LocalTask>> = flow {
        _tasks?.values?.toList()?.let { emit(it) }
    }

    override fun deleteAllTasks() {
        _tasks?.clear()
    }

    override suspend fun updateCompletionDate(id: String, completionDate: Long) {
        _tasks?.get(id)?.let { it.completionDate = completionDate }
    }
}