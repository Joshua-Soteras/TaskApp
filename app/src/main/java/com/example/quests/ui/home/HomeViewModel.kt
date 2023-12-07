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
 * Changes (from architecture sample's TasksViewModel.kt):
 * - Removed filtering feature and network related functions
 * - UiState has lastTaskCompleted for an undo-completion feature
 */

package com.example.quests.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quests.R
import com.example.quests.data.Task
import com.example.quests.data.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val taskList: List<Task> = listOf(),
    val snackbarMessage: Int? = null,
    val lastTaskCompleted: Task? = null,
    var newTaskAvailable: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val _snackbarMessage = MutableStateFlow<Int?>(null)
    private val _lastTaskCompleted = MutableStateFlow<Task?>(null)
    private val _tasks = taskRepository.getAllTasksStream()
    private var lastTaskListSize = -1

    val uiState: StateFlow<HomeUiState> = combine(
        _tasks, _snackbarMessage, _lastTaskCompleted
    ) { tasks, snackbarMessage, lastTaskCompleted ->
        HomeUiState(
            taskList = tasks,
            snackbarMessage = snackbarMessage,
            lastTaskCompleted = lastTaskCompleted,
            // TODO: Probably a better way to handle this without needing lastTaskListSize,
            //  but whatever
            newTaskAvailable =
                if (lastTaskListSize == -1 || lastTaskListSize >= tasks.size) {
                    lastTaskListSize = tasks.size
                    false
                } else {
                    lastTaskListSize = tasks.size
                    true
                }
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            taskRepository.completeTask(task.id)
            setLastTaskCompleted(task)
            setSnackbarMessage(R.string.snackbar_task_completed)
        } else {
            taskRepository.activateTask(task.id)
        }
    }

    fun activateLastTaskCompleted() = viewModelScope.launch {
        _lastTaskCompleted.value?.let { taskRepository.activateTask(it.id) }
    }

    fun resetSnackbarMessage() {
        setSnackbarMessage(null)
    }

    private fun setLastTaskCompleted(task: Task) {
        _lastTaskCompleted.value = task
    }

    private fun setSnackbarMessage(message: Int?) {
        _snackbarMessage.value = message
    }

    fun clearCompletedTasks() = viewModelScope.launch {
        taskRepository.clearCompletedTasks()
    }
}