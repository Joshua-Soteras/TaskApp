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
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BackupUiState(
    val snackbarMessage: Int? = null,
    val authToken: AuthToken? = null, // not sure if this is the best place to put this
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _snackbarMessage = MutableStateFlow<Int?>(null)
    private val _authToken = authRepository.authTokenFlow

    val uiState: StateFlow<BackupUiState> = combine(
        _snackbarMessage, _authToken
    ) { snackbarMessage, authToken ->
        BackupUiState(
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

    fun signOut() {
        viewModelScope.launch {
            authRepository.clearAuthToken()
        }
    }

    fun test() {
        println(_authToken)
        println(uiState.value)
    }

    // before uploading or loading data, refresh the access token
    // if the refresh token is expired, take them to login screen and say that they're
    // credentials have expired and that they need to log in to refresh them
}