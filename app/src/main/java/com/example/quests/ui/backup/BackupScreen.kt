package com.example.quests.ui.backup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quests.R
import com.example.quests.ui.navigation.NavigationDestination
import com.example.quests.ui.theme.QuestsTheme
import com.example.quests.ui.util.BackupTopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object BackupDestination : NavigationDestination {
    override val route = "backup"
    override val titleRes = R.string.backup
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun BackupScreen(
    openDrawer: () -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BackupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .semantics {
                testTagsAsResourceId = true
            },
        topBar = {
            BackupTopAppBar(
                openDrawer = openDrawer,
                scrollBehavior = scrollBehavior,
                onSignOut = { viewModel.signOut() }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->

        uiState.snackbarMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
            viewModel.resetSnackbarMessage()
        }

        if (uiState.authToken == null) {
            NoAuthTokenBackupContent(
                navigateToLogin = navigateToLogin,
                modifier.padding(paddingValues)
            )
        } else {
            BackupContent(
                openUploadDialog = { viewModel.updateUploadAlertDialogIsOpen(true) },
                openLoadDialog = { viewModel.updateLoadAlertDialogIsOpen(true) },
                modifier = modifier.padding(paddingValues)
            )
        }
    }

    if (uiState.uploadAlertDialogIsOpen) {
        BackupConfirmationDialog(
            title = R.string.upload_tasks_confirmation_title,
            text = R.string.upload_tasks_confirmation_description,
            networkAction = viewModel::uploadTasks,
            resetAlertDialog = { viewModel.updateUploadAlertDialogIsOpen(false) }
        )
    } else if (uiState.loadAlertDialogIsOpen) {
        BackupConfirmationDialog(
            title = R.string.load_tasks_confirmation_title,
            text = R.string.load_tasks_confirmation_description,
            networkAction = viewModel::loadTasks,
            resetAlertDialog = { viewModel.updateLoadAlertDialogIsOpen(false) }
        )
    }
}

@Composable
fun NoAuthTokenBackupContent(
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(all = 16.dp) // TODO: extract this to dimen res
    ) {
        Text(stringResource(R.string.not_logged_in_help_message))
        Spacer(modifier = Modifier.size(16.dp)) // TODO: extract as dimen res
        Button(
            onClick = navigateToLogin,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.login))
        }
    }
}

@Composable
fun BackupContent(
    openUploadDialog: () -> Unit,
    openLoadDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(all = 16.dp) // TODO: extract this to dimen res
    ) {
        Button(
            onClick = openUploadDialog,
            modifier = Modifier.fillMaxWidth()
        ) {

            Icon(painter = painterResource(R.drawable.cloud_upload_24px), contentDescription = null)
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(stringResource(R.string.upload_to_network))
        }
        Button(
            onClick = openLoadDialog,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(painter = painterResource(R.drawable.cloud_download_24px), contentDescription = null)
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(stringResource(R.string.load_from_network))
        }
    }
}

@Composable
fun BackupConfirmationDialog(
    title: Int,
    text: Int,
    networkAction: () -> Unit,
    resetAlertDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        title = { Text(stringResource(title)) },
        text = { Text(stringResource(text)) },
        onDismissRequest = resetAlertDialog,
        confirmButton = {
            TextButton(
                onClick = {
                    networkAction()
                    resetAlertDialog()
                },
                modifier = Modifier.testTag("confirm network action")
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    resetAlertDialog()
                },
                modifier = Modifier.testTag("dismiss network action")
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNoAuthTokenBackupContent() {
    QuestsTheme {
        NoAuthTokenBackupContent(navigateToLogin = { })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBackupContent() {
    QuestsTheme {
        BackupContent(openUploadDialog = { }, openLoadDialog = { })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUploadConfirmationDialog() {
    QuestsTheme {
        BackupConfirmationDialog(
            title = R.string.upload_tasks_confirmation_title,
            text = R.string.upload_tasks_confirmation_description,
            networkAction = { },
            resetAlertDialog = { }
        )
    }
}

@Preview
@Composable
fun PreviewLoadConfirmationDialog() {
    QuestsTheme {
        BackupConfirmationDialog(
            title = R.string.load_tasks_confirmation_title,
            text = R.string.load_tasks_confirmation_description,
            networkAction = { },
            resetAlertDialog = { }
        )
    }
}