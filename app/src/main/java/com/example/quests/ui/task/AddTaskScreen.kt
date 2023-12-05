package com.example.quests.ui.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.quests.R
import com.example.quests.ui.navigation.NavigationDestination
import com.example.quests.ui.util.AddTaskTopAppBar
import com.example.quests.ui.util.TimePickerDialog
import com.example.quests.util.toEpochMilli
import com.example.quests.util.toFormattedString
import com.example.quests.util.toLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object AddTaskDestination : NavigationDestination {
    override val route = "add_task"
    override val titleRes = R.string.create_task
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    val openDateDialog = remember { mutableStateOf(false) }
    val openTimeDialog = remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AddTaskTopAppBar(
                onBack = navigateBack
            )
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        AddTaskContent(
            title = uiState.title,
            description = uiState.description,
            isEntryValid = uiState.isEntryValid,
            onTitleChange = viewModel::updateTitle,
            onDescriptionChange = viewModel::updateDescription,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.createTask()
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

@Composable
private fun AddTaskContent(
    title: String,
    description: String,
    isEntryValid: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    date: String?,
    onDatePickerClick: () -> Unit,
    time: String?,
    onTimePickerClick: () -> Unit,
    dateTimeIsLate: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.title_input_label)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.title_24px),
                    contentDescription = null
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        )
        TextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier
                .heightIn(1.dp, Dp.Infinity)
                .fillMaxWidth(),
            label = { Text(stringResource(R.string.description_label_input)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.subject_24px),
                    contentDescription = null
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        )
        // Dumb hack because Compose still gives space to label even if label is null
        DatePickerTextField(date, onDatePickerClick, dateTimeIsLate)
        TimePickerTextField(time, onTimePickerClick, dateTimeIsLate)
        Spacer(modifier = Modifier.size(32.dp)) // TODO: extract this as dimen res
        Button(
            onClick = onSaveClick,
            enabled = isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}

@Composable
private fun disabledTextFieldColors(isLate: Boolean): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    // Disabled colors https://stackoverflow.com/a/76922565
    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    disabledTextColor =
    // This probably isn't want the `error` color is meant for, but whatever, change if ugly
        if (isLate) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.onSurfaceVariant,
    disabledBorderColor = MaterialTheme.colorScheme.outline,
    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledSuffixColor = MaterialTheme.colorScheme.onSurfaceVariant
)

@Composable
private fun DatePickerTextField(
    date: String?,
    onDatePickerClick: () -> Unit,
    dateTimeIsLate: Boolean,
) {
    if (date == null) {
        TextField(
            value = stringResource(R.string.no_due_date),
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDatePickerClick() },
            enabled = false,
            leadingIcon = {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
            },
            colors = disabledTextFieldColors(isLate = dateTimeIsLate)
        )
    } else {
        TextField(
            value = date,
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDatePickerClick() },
            enabled = false,
            label = { Text(stringResource(R.string.due_date)) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
            },
            colors = disabledTextFieldColors(isLate = dateTimeIsLate),
        )
    }
}

@Composable
private fun TimePickerTextField(
    time: String?,
    onTimePickerClick: () -> Unit,
    dateTimeIsLate: Boolean,
) {
    if (time == null) {
        TextField(
            value = stringResource(R.string.no_due_time),
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTimePickerClick() },
            enabled = false,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.schedule_24px),
                    contentDescription = null
                )
            },
            colors = disabledTextFieldColors(isLate = dateTimeIsLate)
        )
    } else {
        TextField(
            value = time,
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTimePickerClick() },
            enabled = false,
            label = { Text(stringResource(R.string.due_time)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.schedule_24px),
                    contentDescription = null
                )
            },
            colors = disabledTextFieldColors(isLate = dateTimeIsLate),
        )
    }
}