package com.upchiapas.appcrud.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.upchiapas.appcrud.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de Registro.
 * Valida: nombre no vacío, correo con formato, contraseña mín. 8 chars, confirmación coincide.
 */
class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // ── Actualización de campos ──────────────────────────────────────────────
    fun onNameChange(value: String) =
        _uiState.update { it.copy(name = value, nameError = null, errorMessage = null) }

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, emailError = null, errorMessage = null) }

    fun onPasswordChange(value: String) =
        _uiState.update { it.copy(password = value, passwordError = null, errorMessage = null) }

    fun onConfirmPasswordChange(value: String) =
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }

    // ── Registro ─────────────────────────────────────────────────────────────
    fun register() {
        if (!validate()) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.register(
                name     = _uiState.value.name.trim(),
                email    = _uiState.value.email.trim(),
                password = _uiState.value.password
            )
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading    = false,
                            errorMessage = error.message ?: "No se pudo completar el registro"
                        )
                    }
                }
            )
        }
    }

    fun onRegisterHandled() {
        _uiState.update { it.copy(registerSuccess = false) }
    }

    // ── Validación ───────────────────────────────────────────────────────────
    private fun validate(): Boolean {
        val s = _uiState.value
        val nameError = if (s.name.isBlank()) "El nombre es obligatorio" else null
        val emailError = when {
            s.email.isBlank() -> "El correo es obligatorio"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(s.email.trim()).matches() ->
                "Formato de correo inválido"
            else -> null
        }
        val passwordError = when {
            s.password.isBlank() -> "La contraseña es obligatoria"
            s.password.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            else -> null
        }
        val confirmPasswordError = when {
            s.confirmPassword.isBlank() -> "Confirma tu contraseña"
            s.confirmPassword != s.password -> "Las contraseñas no coinciden"
            else -> null
        }

        _uiState.update {
            it.copy(
                nameError            = nameError,
                emailError           = emailError,
                passwordError        = passwordError,
                confirmPasswordError = confirmPasswordError
            )
        }
        return nameError == null && emailError == null &&
               passwordError == null && confirmPasswordError == null
    }

    companion object {
        fun factory(authRepository: AuthRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { RegisterViewModel(authRepository) }
            }
    }
}
