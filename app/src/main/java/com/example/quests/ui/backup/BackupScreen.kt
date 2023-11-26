package com.example.quests.ui.backup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
    ) {
        viewModel.test()
        println("on backup screen")

        if (uiState.authToken == null) {
            NoAuthTokenBackupContent(navigateToLogin = navigateToLogin, modifier.padding(it))
        } else {
            Text("backup screen", modifier.padding(it))
        }

        // if authtoken is null, say that you need to login to use this service, button to that page
        // if authtoken is not null, display backup ui
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

@Preview(showBackground = true)
@Composable
fun PreviewNoAuthTokenBackupContent() {
    QuestsTheme {
        NoAuthTokenBackupContent(navigateToLogin = { })
    }
}