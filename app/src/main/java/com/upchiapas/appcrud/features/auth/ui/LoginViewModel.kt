package com.upchiapas.appcrud.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.upchiapas.appcrud.features.auth.domain.SessionStorage
import com.upchiapas.appcrud.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de Login.
 * Toda la lógica y validación vive aquí — el Composable solo observa y envía eventos.
 */
class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // ── Actualización de campos ──────────────────────────────────────────────
    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, errorMessage = null) }
    }

    // ── Login ────────────────────────────────────────────────────────────────
    fun login() {
        if (!validate()) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.login(
                email    = _uiState.value.email.trim(),
                password = _uiState.value.password
            )
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading    = false,
                            errorMessage = error.message ?: "Credenciales incorrectas"
                        )
                    }
                }
            )
        }
    }

    /** Limpia la bandera de éxito después de que la navegación se ejecutó */
    fun onLoginHandled() {
        _uiState.update { it.copy(loginSuccess = false) }
    }

    // ── Validación ───────────────────────────────────────────────────────────
    private fun validate(): Boolean {
        val state = _uiState.value
        var valid = true

        val emailError = when {
            state.email.isBlank() -> "El correo es obligatorio"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches() ->
                "Formato de correo inválido"
            else -> null
        }
        val passwordError = if (state.password.isBlank()) "La contraseña es obligatoria" else null

        _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
        if (emailError != null || passwordError != null) valid = false

        return valid
    }

    companion object {
        /** Factory para DI manual con viewModelFactory { initializer { } } */
        fun factory(
            authRepository: AuthRepository,
            sessionStorage: SessionStorage
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                LoginViewModel(authRepository, sessionStorage)
            }
        }
    }
}
