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
 * Changes:
 * - Pretty much everything. Most noticeably, TopAppBars.kt in the
 *   architecture sample uses the older androidx.compose.material.TopAppBar
 *   while we use androidx.compose.material3.TopAppBar
 */

@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.quests.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.quests.R

@Composable
fun HomeTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    openDrawer: () -> Unit,
    clearCompletedTasks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val openAlertDialog = remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.home_destination_title),
                style = MaterialTheme.typography.displayMedium
            ) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.open_drawer)
                )
            }
        },
        actions = {
            HomeDropdownMenu(
                onClearCompletedTasks = { openAlertDialog.value = !openAlertDialog.value },
            )
        }
    )

    if (openAlertDialog.value) {
        AlertDialog(
            title = { Text(stringResource(R.string.clear_completed_tasks_dialog)) },
            onDismissRequest = { openAlertDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        clearCompletedTasks()
                        openAlertDialog.value = false
                    },
                    modifier = Modifier.testTag("confirm clear completed tasks")
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openAlertDialog.value = false
                    },
                    modifier = Modifier.testTag("dismiss clear completed tasks")
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun HomeDropdownMenu(
    onClearCompletedTasks: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.testTag("home dropdown button")
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.dropdown_menu_description),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.clear_completed))},
                onClick = { onClearCompletedTasks(); expanded = !expanded },
                modifier = Modifier.testTag("clear completed button")
            )
        }
    }
}

@Composable
fun AddTaskTopAppBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.create_task)) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        },
    )
}

@Composable
fun TaskDetailTopAppBar(
    onBack: () -> Unit,
    deleteTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val openAlertDialog = remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = stringResource(R.string.edit_task)) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        },
        actions = {
            IconButton(onClick = { openAlertDialog.value = !openAlertDialog.value }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete)
                )
            }
        }
    )

    if (openAlertDialog.value) {
        AlertDialog(
            title = { Text(stringResource(R.string.delete_this_task)) },
            onDismissRequest = { openAlertDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteTask()
                        openAlertDialog.value = false
                    },
                    modifier = Modifier.testTag("confirm delete task")
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openAlertDialog.value = false
                    },
                    modifier = Modifier.testTag("dismiss delete task")
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun BackupTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    openDrawer: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.backup),
                style = MaterialTheme.typography.displayMedium
            ) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.open_drawer)
                )
            }
        },
        actions = {
            BackupDropdownMenu(
                onSignOut = onSignOut
            )
        }
    )
}

@Composable
fun BackupDropdownMenu(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.testTag("backup dropdown button")
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.dropdown_menu_description),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.sign_out))},
                onClick = { onSignOut(); expanded = !expanded },
                modifier = Modifier.testTag("sign out button")
            )
        }
    }
}

@Composable
fun LoginTopAppBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.login)) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        },
    )
}

@Composable
fun SignupTopAppBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.sign_up)) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        },
    )
}

@Preview
@Composable
private fun HomeTopAppBarPreview() {
    Surface {
        HomeTopAppBar(openDrawer = { }, clearCompletedTasks = { })
    }
}

@Preview
@Composable
private fun HomeDropdownMenuPreview() {
    Surface {
        HomeDropdownMenu(onClearCompletedTasks = { })
    }
}

@Preview
@Composable
private fun AddTaskTopAppBarPreview() {
    Surface {
        AddTaskTopAppBar({ })
    }
}

@Preview
@Composable
private fun BackupTopAppBarPreview() {
    Surface {
        BackupTopAppBar(openDrawer = { }, onSignOut = { })
    }
}

@Preview
@Composable
private fun LoginTopAppBarPreview() {
    Surface {
        LoginTopAppBar(onBack = { })
    }
}

@Preview
@Composable
private fun SignupTopAppBarPreview() {
    Surface {
        SignupTopAppBar(onBack = { })
    }
}