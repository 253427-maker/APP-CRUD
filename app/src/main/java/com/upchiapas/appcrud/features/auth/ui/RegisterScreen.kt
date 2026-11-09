package com.upchiapas.appcrud.features.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
 * Pantalla de Registro — RF-01.
 * Solo observa el estado y delega eventos al ViewModel.
 */
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            viewModel.onRegisterHandled()
            onRegisterSuccess()
        }
    }

    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text  = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text     = "Completa los datos para registrarte",
            style    = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 32.dp, top = 8.dp)
        )

        // Nombre
        OutlinedTextField(
            value           = uiState.name,
            onValueChange   = viewModel::onNameChange,
            label           = { Text("Nombre completo") },
            leadingIcon     = { Icon(Icons.Default.Person, contentDescription = null) },
            isError         = uiState.nameError != null,
            supportingText  = uiState.nameError?.let { { Text(it) } },
            modifier        = Modifier.fillMaxWidth(),
            singleLine      = true
        )

        Spacer(Modifier.height(12.dp))

        // Correo
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

        // Contraseña
        OutlinedTextField(
            value         = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label         = { Text("Contraseña (mín. 8 caracteres)") },
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

        Spacer(Modifier.height(12.dp))

        // Confirmar contraseña
        OutlinedTextField(
            value         = uiState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label         = { Text("Confirmar contraseña") },
            leadingIcon   = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            isError        = uiState.confirmPasswordError != null,
            supportingText = uiState.confirmPasswordError?.let { { Text(it) } },
            modifier       = Modifier.fillMaxWidth(),
            singleLine     = true
        )

        Spacer(Modifier.height(16.dp))

        if (uiState.errorMessage != null) {
            Card(
                colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
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

        Button(
            onClick  = viewModel::register,
            enabled  = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Registrarse")
            }
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}
