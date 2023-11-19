package com.example.quests.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quests.R
import com.example.quests.data.Task
import com.example.quests.ui.navigation.NavigationDestination
import com.example.quests.ui.theme.QuestsTheme
import com.example.quests.ui.util.HomeTopAppBar

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home_destination_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopAppBar(scrollBehavior = scrollBehavior)
        }
    ) { paddingValues ->
        HomeContent(
            taskList = homeUiState.taskList,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
    }
}

@Composable
private fun HomeContent(
    taskList: List<Task>,
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
    modifier: Modifier = Modifier
) {
    Column(
        // TODO: extract to dimensionResource
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = task.title,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = task.description,
            style = MaterialTheme.typography.titleMedium
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
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentEmptyListPreview() {
    QuestsTheme {
        HomeContent(listOf())
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemPreview() {
    QuestsTheme {
        TaskItem(
            task = Task("1", "item title", "a description")
        )
    }
}