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
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _snackbarMessage = MutableStateFlow<Int?>(null)
    private val _lastTaskCompleted = MutableStateFlow<Task?>(null)
    private val _tasks = taskRepository.getAllTasksStream()

    val uiState: StateFlow<HomeUiState> = combine(
        _tasks, _snackbarMessage, _lastTaskCompleted
    ) { tasks, snackbarMessage, lastTaskCompleted ->
        HomeUiState(tasks, snackbarMessage, lastTaskCompleted)
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