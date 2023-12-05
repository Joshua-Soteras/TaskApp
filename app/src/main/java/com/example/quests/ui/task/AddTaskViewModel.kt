package com.example.quests.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quests.data.TaskRepository
import com.example.quests.util.atNullableTime
import com.example.quests.util.isWithinToday
import com.example.quests.util.toEpochMilli
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

data class AddTaskUiState(
    val title: String = "",
    val description: String = "",
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
//    val userMessage: Int? = null, don't think we use this for anything
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

    fun updateSelectedDate(newSelectedDate: LocalDate?) {
        _uiState.update {
            it.copy(selectedDate = newSelectedDate)
        }
        // If there is date is null, time is null
        if (newSelectedDate == null) {
            updateSelectedTime(null)
        }
        validateEntry()
    }

    fun updateSelectedTime(newSelectedTime: LocalTime?) {
        _uiState.update {
            it.copy(selectedTime = newSelectedTime)
        }
        // If newSelectedTime is not null and selectedDate is null, choose a new selectedDate
        // depending on the current time.
        val selectedDate = _uiState.value.selectedDate
        if (newSelectedTime != null && selectedDate == null) {
            if (newSelectedTime.isWithinToday()) {
                updateSelectedDate(LocalDate.now()) // Today
            } else {
                updateSelectedDate(LocalDate.now().plusDays(1)) // Tomorrow
            }
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
            taskRepository.createTask(
                title = uiState.value.title,
                description = uiState.value.description,
                dueDate = uiState.value.selectedDate
                    ?.atNullableTime(uiState.value.selectedTime)
                    ?.toEpochMilli()
                    ?: 0L
            )
            _uiState.update {
                it.copy(isTaskSaved = true)
            }
        }
    }
}