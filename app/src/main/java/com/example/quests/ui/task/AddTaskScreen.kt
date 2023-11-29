package com.example.quests.ui.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object AddTaskDestination : NavigationDestination {
    override val route = "add_task"
    override val titleRes = R.string.create_task
}

@Composable
fun AddTaskScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    viewModel: AddTaskViewModel = hiltViewModel()
) {
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
            modifier = Modifier.padding(paddingValues)
        )
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            // TODO: extract this to dimen res
            // .padding(all = 16.dp)
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