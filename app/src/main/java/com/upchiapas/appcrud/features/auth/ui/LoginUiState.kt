package com.upchiapas.appcrud.features.auth.ui

/** Estado inmutable de la pantalla de Login */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,   // error de red o credenciales inválidas
    val emailError: String? = null,     // validación de campo
    val passwordError: String? = null,  // validación de campo
    val loginSuccess: Boolean = false   // dispara navegación a Home
)
