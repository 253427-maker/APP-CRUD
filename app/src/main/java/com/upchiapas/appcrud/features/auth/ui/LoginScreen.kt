package com.upchiapas.appcrud.features.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Pantalla de Login — solo observa estado y envía eventos al ViewModel.
 * Sin lógica ni validaciones aquí (todo en LoginViewModel).
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Efecto de navegación — se ejecuta una vez cuando loginSuccess se pone en true
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            viewModel.onLoginHandled()
            onLoginSuccess()
        }
    }

    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Título ──────────────────────────────────────────────────────
        Text(
            text  = "Control de Cultivos",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text  = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 32.dp, top = 8.dp)
        )

        // ── Campo Correo ─────────────────────────────────────────────────
        OutlinedTextField(
            value           = uiState.email,
            onValueChange   = viewModel::onEmailChange,
            label           = { Text("Correo electrónico") },
            leadingIcon     = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError         = uiState.emailError != null,
            supportingText  = uiState.emailError?.let { { Text(it) } },
            modifier        = Modifier.fillMaxWidth(),
            singleLine      = true
        )

        Spacer(Modifier.height(12.dp))

        // ── Campo Contraseña ─────────────────────────────────────────────
        OutlinedTextField(
            value         = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label         = { Text("Contraseña") },
            leadingIcon   = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = if (passwordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "Ocultar" else "Ver")
                }
            },
            isError        = uiState.passwordError != null,
            supportingText = uiState.passwordError?.let { { Text(it) } },
            modifier       = Modifier.fillMaxWidth(),
            singleLine     = true
        )

        Spacer(Modifier.height(16.dp))

        // ── Error general (red / credenciales) ───────────────────────────
        if (uiState.errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text     = uiState.errorMessage!!,
                    color    = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        // ── Botón Login ──────────────────────────────────────────────────
        Button(
            onClick  = viewModel::login,
            enabled  = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color    = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Iniciar sesión")
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Ir a Registro ────────────────────────────────────────────────
        TextButton(onClick = onNavigateToRegister) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
