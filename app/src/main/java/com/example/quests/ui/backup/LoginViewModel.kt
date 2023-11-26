package com.example.quests.ui.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quests.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val snackbarMessage: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _snackbarMessage = MutableStateFlow<String?>(null)

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(LoginUiState())
    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<LoginUiState> = _uiState.combine(
        _snackbarMessage
    ) { uiState, snackbarMessage ->
        uiState.copy(snackbarMessage = snackbarMessage)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = LoginUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    // TODO: [tentative] The backend currently doesn't have any restrictions on what the username
    //  or password should be. You can currently sign up with an empty username / password, or
    //  use whatever UTF-8 characters you want.
    //  If we do add restrictions to this in the backend, we should have a validateEntry()
    //  function like we did in AddTaskViewModel.

    fun updateUsername(newUsername: String) {
        _uiState.update {
            it.copy(username = newUsername)
        }
    }

    fun updatePassword(newPassword: String) {
        _uiState.update {
            it.copy(password = newPassword)
        }
    }

    fun resetSnackbarMessage() {
        setSnackbarMessage(null)
    }

    private fun setSnackbarMessage(message: String?) {
        _snackbarMessage.value = message
    }

    fun login(navigateToBackup: () -> Unit) {
        viewModelScope.launch {
            authRepository.login(
                username = uiState.value.username,
                password = uiState.value.password,
                onComplete = navigateToBackup,
                onError = { setSnackbarMessage(it) },
            )
        }
    }
}