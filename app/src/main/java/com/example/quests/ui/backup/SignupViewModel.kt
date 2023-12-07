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

data class SignupUiState(
    val username: String = "",
    val password: String = "",
    val snackbarMessage: String? = null,
)

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _snackbarMessage = MutableStateFlow<String?>(null)

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(SignupUiState())
    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<SignupUiState> = _uiState.combine(
        _snackbarMessage
    ) { uiState, snackbarMessage ->
        uiState.copy(snackbarMessage = snackbarMessage)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = SignupUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    // TODO: [tentative] see TODO message in LoginViewModel.kt, we could have restrictions on
    //  usernames and passwords in the future. In that case, we should have client-side validation
    //  to save resources.

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

    fun register(navigateOnSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.register(
                username = uiState.value.username,
                password = uiState.value.password,
                onComplete = navigateOnSuccess,
                onError = { setSnackbarMessage(it) },
            )
        }
    }
}