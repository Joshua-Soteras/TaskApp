/*
 * Copyright 2022 The Android Open Source Project
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
 * Changes (from architecture sample's TasksScreen.kt):
 * - No filtering
 * - Wrap item contents in a Card
 * - Show due date-time for tasks with due date-time
 */

package com.example.quests.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quests.R
import com.example.quests.data.Task
import com.example.quests.ui.navigation.NavigationDestination
import com.example.quests.ui.theme.QuestsTheme
import com.example.quests.ui.util.HomeTopAppBar
import com.example.quests.util.toFormattedString
import com.example.quests.util.toLocalDateTime
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
    onTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    if (uiState.newTaskAvailable) {
        LaunchedEffect(lazyListState) {
            lazyListState.scrollToItem(uiState.taskList.lastIndex)
        }
        // Set this to null after composition
        uiState.newTaskAvailable = false
    }

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
            onTaskClick = onTaskClick,
            lazyListState = lazyListState,
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
    onTaskClick: (Task) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
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
                state = lazyListState,
                horizontalAlignment = Alignment.Start,
                // Have to use Modifier instead of the passed modifier here or else there will
                // be large padding on top of the list. No idea what's causing that.
                // EDIT: think I know what was causing that, lowercase `modifier` has the
                // paddingValues from the Scaffold which is applied to not make the TopAppBar
                // cover the composable. So don't use `modifier` from the Scaffold.
                modifier = Modifier.fillMaxSize()
            ) {
                items(taskList) {task ->
                    TaskItem(
                        task = task,
                        onCheckedChange = { onTaskCheckedChange(task, it) },
                        onTaskClick = onTaskClick,
                        modifier = Modifier
                            // TODO: extract as dimensionResource
                            .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                            .fillMaxWidth()
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
    onTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = modifier
    ) {
        Row(
            modifier = modifier.clickable { onTaskClick(task) }
        ) {
            Column(
                // TODO: extract to dimensionResource
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = onCheckedChange,
                        modifier = Modifier.align(Alignment.Top)
                    )
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (task.isCompleted) {
                            TextDecoration.LineThrough
                        } else {
                            null
                        },
                        // TODO: probably need to change this if there's a dark mode
                        //  use the color from a color scheme or something?
                        color = if (task.isCompleted) {
                            MaterialTheme.colorScheme.outline
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1, // get rid of this is if you want multiline title
                        modifier = Modifier
                            .weight(2f)
                            .align(Alignment.CenterVertically)
                    )
                    if (task.hasDueDate) {
                        Spacer(Modifier.weight(1f))
                        Text(
                            task.dueDate.toLocalDateTime().toFormattedString(),
                            maxLines = 1,
                            textAlign = TextAlign.Right,
                            color = if (!task.isCompleted && task.isLate) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.outline
                            },
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 8.dp),
                            fontSize = 12.sp
                        )
                    }
                    if (task.description.isNotEmpty()){
                        ExpandButton(
                            expanded = expanded,
                            onClick = { expanded = !expanded }
                        )
                    }
                }
                if (task.description.isNotEmpty() && expanded) {
                    Row {
                        // TODO: Default size of checkbox is 20.dp, we double it (idk why), then
                        //  the spacing in row scope is 8, so we add 8? No idea, but it works...
                        //  This is still an awful solution because it's hardcoded.
                        //  https://stackoverflow.com/a/75358237
                        Spacer(Modifier.width((2*20 + 8).dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(end=8.dp, bottom=8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandButton(
    expanded : Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = if (expanded) painterResource(R.drawable.expand_less_24px)
                else painterResource(R.drawable.expand_more_24px),
            contentDescription = null
        )
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
            onTaskCheckedChange = { _, _ -> },
            onTaskClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentEmptyListPreview() {
    QuestsTheme {
        HomeContent(
            taskList= listOf(),
            onTaskCheckedChange = { _, _ -> },
            onTaskClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemPreview() {
    QuestsTheme {
        TaskItem(
            task = Task("1", "item title", "a description"),
            onCheckedChange = { },
            onTaskClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemCompletedPreview() {
    QuestsTheme {
        TaskItem(
            task = Task("1", "item title", "a description", completionDate = 1L),
            onCheckedChange = { },
            onTaskClick = { }
        )
    }
}