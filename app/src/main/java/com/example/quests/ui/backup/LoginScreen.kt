package com.example.quests.ui.backup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.quests.R
import com.example.quests.ui.navigation.NavigationDestination
import com.example.quests.ui.theme.QuestsTheme
import com.example.quests.ui.util.LoginTopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object LoginDestination : NavigationDestination {
    override val route = "login"
    override val titleRes = R.string.login
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    navigateBack: () -> Unit,
    navigateToSignup: () -> Unit,
    messageFromSignup: String?,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var messageFromSignup = remember { messageFromSignup }

    // After we consume the messageFromSignup, set it to null so we don't use it again
    if (messageFromSignup != null) {
        viewModel.setSnackbarMessage(messageFromSignup)
        messageFromSignup = null
    }

    Scaffold(
        modifier = modifier
            .semantics {
                testTagsAsResourceId = true
            },
        topBar = {
            LoginTopAppBar(
                onBack = navigateBack
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

        LoginContent(
            username = uiState.username,
            password = uiState.password,
            onUsernameChange = viewModel::updateUsername,
            onPasswordChange = viewModel::updatePassword,
            onLogin = { viewModel.login(navigateBack) },
            onSignup = navigateToSignup,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun LoginContent(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onSignup: () -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisibility: Boolean by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier
            .fillMaxWidth()
            // TODO: extract this to dimen res
            .padding(all = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.username)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        )
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.password)) },
            visualTransformation =
                if (passwordVisibility) VisualTransformation.None
                else PasswordVisualTransformation(),
            trailingIcon = {
                  IconButton(onClick = {
                      passwordVisibility = !passwordVisibility
                  }) {
                      Icon(
                          painter =
                            if (passwordVisibility) painterResource(id = R.drawable.visibility_24px)
                            else painterResource(id = R.drawable.visibility_off_24px),
                          contentDescription = null
                      )
                  }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        )
        Spacer(modifier = Modifier.size(32.dp)) // TODO: extract this as dimen res
        Button(
            onClick = { focusManager.clearFocus(); onLogin() },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.login))
        }
        OutlinedButton(
            onClick = { focusManager.clearFocus(); onSignup() },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.sign_up))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginContent() {
    QuestsTheme {
        LoginContent(
            username = "name",
            password = "password",
            onUsernameChange = { },
            onPasswordChange = { },
            onLogin = { },
            onSignup = { }
        )
    }
}