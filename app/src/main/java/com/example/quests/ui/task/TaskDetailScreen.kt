package com.example.quests.ui.task

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.quests.R
import com.example.quests.ui.navigation.NavigationDestination
import com.example.quests.ui.util.TaskDetailTopAppBar
import com.example.quests.ui.util.TimePickerDialog
import com.example.quests.util.toEpochMilli
import com.example.quests.util.toFormattedString
import com.example.quests.util.toLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object TaskDetailDestination : NavigationDestination {
    override val route = "task/{taskId}"
    override val titleRes = R.string.edit_task
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    navigateBack: () -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val openDateDialog = remember { mutableStateOf(false) }
    val openTimeDialog = remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaskDetailTopAppBar(
                onBack = navigateBack,
                deleteTask = viewModel::deleteTask
            )
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState.isTaskDeleted) {
            if (uiState.isTaskDeleted) {
                onDeleteTask()
            }
        }

        AddTaskContent(
            title = uiState.title,
            description = uiState.description,
            isEntryValid = uiState.isEntryValid,
            onTitleChange = viewModel::updateTitle,
            onDescriptionChange = viewModel::updateDescription,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveTask()
                    navigateBack()
                }
            },
            // n.b., time is not null only if date is not null, but date can be not null
            // while time is null
            date = uiState.selectedDate?.toFormattedString(),
            onDatePickerClick = { openDateDialog.value = true },
            time = uiState.selectedTime?.toFormattedString(),
            onTimePickerClick = { openTimeDialog.value = true },
            dateTimeIsLate = uiState.selectedDateTimeIsLate,
            modifier = Modifier.padding(paddingValues)
        )

        if (openDateDialog.value) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = uiState.selectedDate?.atStartOfDay()?.toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { openDateDialog.value = false },
                // Dumb hack, the dialog does not resize to fit other composables so we're
                // squeezing all the buttons into one row
                confirmButton = {
                    Row {
                        TextButton(onClick = { datePickerState.setSelection(null) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.block_24px),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                            Text(stringResource(R.string.no_date))
                        }
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = { openDateDialog.value = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                        TextButton(
                            onClick = {
                                openDateDialog.value = false
                                viewModel.updateSelectedDate(
                                    datePickerState.selectedDateMillis?.toLocalDate()
                                )
                            },
                        ) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }

        if (openTimeDialog.value) {
            TimePickerDialog(
                onCancel = { openTimeDialog.value = false },
                onConfirm = {
                    openTimeDialog.value = false
                    viewModel.updateSelectedTime(it)
                },
                setNoTime = {
                    openTimeDialog.value = false
                    viewModel.updateSelectedTime(null)
                },
                initial = uiState.selectedTime
            )
        }
    }
}