package com.example.quests.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quests.R
import com.example.quests.data.Task
import com.example.quests.ui.navigation.NavigationDestination
import com.example.quests.ui.theme.QuestsTheme
import com.example.quests.ui.util.HomeTopAppBar
import kotlinx.coroutines.launch

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home_destination_title
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
    onAddTask: () -> Unit,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .semantics {
                testTagsAsResourceId = true
            },
        topBar = {
            HomeTopAppBar(
                scrollBehavior = scrollBehavior,
                openDrawer = openDrawer,
                clearCompletedTasks = viewModel::clearCompletedTasks,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.create_task)
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->

        uiState.snackbarMessage?.let { message ->
            val stringMessage = stringResource(message)
            val undoMessage = stringResource(R.string.snackbar_undo)
            scope.launch {
                val result = snackbarHostState
                    .showSnackbar(
                        message = stringMessage,
                        actionLabel = undoMessage,
                        duration = SnackbarDuration.Short
                    )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        viewModel.activateLastTaskCompleted()
                    }
                    SnackbarResult.Dismissed -> { }
                }
            }
            viewModel.resetSnackbarMessage()
        }

        HomeContent(
            taskList = uiState.taskList,
            onTaskCheckedChange = viewModel::completeTask,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .testTag("HomeContent")
        )
    }
}

@Composable
private fun HomeContent(
    taskList: List<Task>,
    onTaskCheckedChange: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (taskList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_tasks_in_the_database),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        } else {
            LazyColumn(
                horizontalAlignment = Alignment.Start,
                // Have to use Modifier instead of the passed modifier here or else there will
                // be large padding on top of the list. No idea what's causing that
                modifier = Modifier.fillMaxSize()
            ) {
                items(taskList) {task ->
                    TaskItem(
                        task = task,
                        onCheckedChange = { onTaskCheckedChange(task, it) },
                        modifier = Modifier
                            // TODO: extract as dimensionResource
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row {
        val undoMessage = stringResource(R.string.snackbar_undo)
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.alignByBaseline()
        )
        Column(
            // TODO: extract to dimensionResource
            modifier = Modifier.padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge,
                textDecoration = if (task.isCompleted) {
                    TextDecoration.LineThrough
                } else {
                    null
                },
                // TODO: probably need to change this if there's a dark mode
                //  use the color from a color scheme or something?
                color = if (task.isCompleted) {
                    Color.Gray
                } else {
                    Color.Black
                }
            )
            Text(
                text = task.description,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    QuestsTheme {
        HomeContent(
            taskList = listOf(
                Task("1", "title1", "desc1"),
                Task("2", "title2", "desc2")
            ),
            onTaskCheckedChange = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentEmptyListPreview() {
    QuestsTheme {
        HomeContent(
            taskList= listOf(),
            onTaskCheckedChange = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemPreview() {
    QuestsTheme {
        TaskItem(
            task = Task("1", "item title", "a description"),
            onCheckedChange = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemCompletedPreview() {
    QuestsTheme {
        TaskItem(
            task = Task("1", "title", "description", completionDate = 1L),
            onCheckedChange = { }
        )
    }
}