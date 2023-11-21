package com.example.quests.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quests.data.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// change this to have a isEntryValid: Boolean field
data class AddTaskUiState(
    val title: String = "",
    val description: String = "",
    val userMessage: Int? = null,
    val isEntryValid: Boolean = false,
    val isTaskSaved: Boolean = false
)

/**
 * ViewModel for add task screen.
 */
@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
) : ViewModel() {
    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(AddTaskUiState())
    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

    fun updateTitle(newTitle: String) {
        _uiState.update {
            it.copy(title = newTitle)
        }
        validateEntry()
    }

    fun updateDescription(newDescription: String) {
        _uiState.update {
            it.copy(description = newDescription)
        }
        validateEntry()
    }

    private fun validateEntry() {
        _uiState.update {
            it.copy(isEntryValid = it.title.isNotBlank())
        }
    }

    fun createTask() {
        viewModelScope.launch {
            taskRepository.createTask(uiState.value.title, uiState.value.description)
            _uiState.update {
                it.copy(isTaskSaved = true)
            }
        }
    }
}