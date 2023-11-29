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

data class AddTaskUiState(
    val title: String = "",
    val description: String = "",
    val userMessage: Int? = null,
    val selectedDate: Long? = null,
    val selectedHour: Int? = null,
    val selectedMinute: Int? = null,
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

    fun updateSelectedDate(newSelectedDate: Long?) {
        _uiState.update {
            it.copy(selectedDate = newSelectedDate)
        }
        validateEntry()
    }

    // give users option to choose no date and no time
    // they can choose yes date, no time (the time will be assumed to be last ms of day)
    // they cannot choose yes time, not date. if they choose any time, automatically
    // choose tomorrow for them. if they try to set it to no date, set it to no time.
    //
    // take the current time, and the passed time. if the hour and minute of the passed
    // time is greater than current time, then set due date to today. if hour and
    // minute of passed time is less than current time, then set due date to tomorrow.

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