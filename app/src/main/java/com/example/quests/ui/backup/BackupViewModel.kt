package com.example.quests.ui.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quests.data.AuthRepository
import com.example.quests.data.AuthToken
import com.example.quests.data.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BackupUiState(
    val snackbarMessage: String? = null,
    val authToken: AuthToken? = null, // not sure if this is the best place to put this
    var uploadAlertDialogIsOpen: Boolean = false,
    var loadAlertDialogIsOpen: Boolean = false,
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    private val _authToken = authRepository.authTokenFlow

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = combine(
        _uiState, _snackbarMessage, _authToken
    ) { uiState, snackbarMessage, authToken ->
        uiState.copy(
            snackbarMessage = snackbarMessage,
            authToken = if (authToken.isEmpty()) null else authToken
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = BackupUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateUploadAlertDialogIsOpen(newBoolean: Boolean) {
        _uiState.update {
            it.copy(uploadAlertDialogIsOpen = newBoolean)
        }
    }

    fun updateLoadAlertDialogIsOpen(newBoolean: Boolean) {
        _uiState.update {
            it.copy(loadAlertDialogIsOpen = newBoolean)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.clearAuthToken()
        }
    }

    fun resetSnackbarMessage() {
        setSnackbarMessage(null)
    }

    private fun setSnackbarMessage(message: String?) {
        _snackbarMessage.value = message
    }

    fun uploadTasks() {
        viewModelScope.launch {
            taskRepository.saveTasksToNetwork(
                // TODO: extract as string resource
                onComplete = { setSnackbarMessage("Successfully uploaded tasks to network") },
                onError = { setSnackbarMessage(it) }
            )
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            taskRepository.loadTasksFromNetwork(
                onComplete = { setSnackbarMessage("Successfully loaded tasks") },
                onError = { setSnackbarMessage(it) }
            )
        }
    }
}