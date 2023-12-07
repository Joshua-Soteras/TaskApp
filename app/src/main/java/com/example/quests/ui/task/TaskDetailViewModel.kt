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
 * Changes (from architecture sample's AddEditTaskViewModel.kt):
 * - This file is only for editing tasks, we reuse parts from
 *   AddTaskViewModel
 * - Different UiState because of the feature of setting due date
 *   and times
 * - Different functions for updating tasks because of how our task repository
 *   works
 */

package com.example.quests.ui.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quests.data.Task
import com.example.quests.data.TaskRepository
import com.example.quests.util.atNullableTime
import com.example.quests.util.isAtEndOfDay
import com.example.quests.util.isWithinToday
import com.example.quests.util.toEpochMilli
import com.example.quests.util.toLocalDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

data class TaskDetailUiState(
    val title: String = "",
    val description: String = "",
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val selectedDateTimeIsLate: Boolean = false,
//    val userMessage: Int? = null, don't think we use this for anything
    val isEntryValid: Boolean = true,
    val completionDate: Long = 0L,
    val isTaskSaved: Boolean = false,
    val isTaskDeleted: Boolean = false
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val taskId: String = checkNotNull(savedStateHandle["taskId"])

    init {
        viewModelScope.launch {
            val task = taskRepository.getTaskStream(taskId).first()
            _uiState.update {
                it.copy(
                    title = task?.title ?: "",
                    description = task?.description ?: "",
                    selectedDate =
                        if (task?.hasDueDate == true)
                            task.dueDate.toLocalDateTime().toLocalDate()
                        else null,
                    selectedTime =
                        if (task?.hasDueDate == false
                                || task?.dueDate?.toLocalDateTime()?.isAtEndOfDay() == true)
                            null
                        else task?.dueDate?.toLocalDateTime()?.toLocalTime(),
                    completionDate = task?.completionDate ?: 0
                )
            }
        }
    }

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(TaskDetailUiState())
    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

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
        checkSelectedDateTimeIsLate()
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
        checkSelectedDateTimeIsLate()
        validateEntry()
    }

    private fun checkSelectedDateTimeIsLate() {
        _uiState.update {
            it.copy(
                selectedDateTimeIsLate = uiState.value.selectedDate
                    ?.atNullableTime(uiState.value.selectedTime)
                    ?.isBefore(LocalDateTime.now())
                    ?: false
            )
        }
    }

    private fun validateEntry() {
        _uiState.update {
            it.copy(isEntryValid = it.title.isNotBlank())
        }
    }

    private fun constructExternalTask(): Task = Task(
        id = taskId,
        title = uiState.value.title,
        description = uiState.value.description,
        dueDate = uiState.value.selectedDate
            ?.atNullableTime(uiState.value.selectedTime)
            ?.toEpochMilli()
            ?: 0L,
        completionDate = uiState.value.completionDate
    )

    fun saveTask() {
        viewModelScope.launch {
            taskRepository.updateTask(constructExternalTask())
            _uiState.update {
                it.copy(isTaskSaved = true)
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            taskRepository.deleteTask(constructExternalTask())
            _uiState.update {
                it.copy(isTaskDeleted = true)
            }
        }
    }
}