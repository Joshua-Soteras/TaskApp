/*
 * Copyright 2019 The Android Open Source Project
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
 * - Different function signatures and contents for insert(), insertAll(),
 *   update(), delete(), and updateCompletionDate() because of differences
 *   in our TaskDao
 */

package com.example.quests.data.source.local

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

    override suspend fun insertAll(tasks: List<LocalTask>) {
        for (t in tasks) {
            insert(t)
        }
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

    override fun getAllTasksAsList(): List<LocalTask> {
        if (tasks == null) {
            println("`tasks` list is null")
            return listOf()
        }
        return tasks as List<LocalTask>
    }

    override fun deleteAllTasks() {
        _tasks?.clear()
    }

    override suspend fun updateCompletionDate(id: String, completionDate: Long) {
        _tasks?.get(id)?.let { it.completionDate = completionDate }
    }

    override suspend fun deleteCompletedTasks() {
        _tasks?.entries?.retainAll { (_, v) -> v.completionDate == 0L }
    }
}