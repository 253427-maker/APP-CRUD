package com.upchiapas.appcrud.features.parcelas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Formulario reutilizable — RF-07 Crear y RF-08 Editar.
 * Si parcelaId == null → modo crear.
 * Si parcelaId != null → modo editar (carga datos del ViewModel al inicio).
 *
 * Los campos viven en el ViewModel → sobreviven a la rotación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelaFormScreen(
    viewModel: ParcelaFormViewModel,
    parcelaId: Int?,               // null = crear, Int = editar
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Cargar datos solo en modo editar y solo la primera vez
    LaunchedEffect(parcelaId) {
        if (parcelaId != null) {
            viewModel.loadParcela(parcelaId)
        }
    }

    // Navegar de regreso cuando se guarda con éxito
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.onSaveHandled()
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (parcelaId == null) "Nueva parcela" else "Editar parcela") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Nombre ──────────────────────────────────────────────────────
            OutlinedTextField(
                value           = uiState.nombre,
                onValueChange   = viewModel::onNombreChange,
                label           = { Text("Nombre de la parcela") },
                isError         = uiState.nombreError != null,
                supportingText  = uiState.nombreError?.let { { Text(it) } },
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true
            )

            // ── Cultivo ──────────────────────────────────────────────────────
            OutlinedTextField(
                value           = uiState.cultivo,
                onValueChange   = viewModel::onCultivoChange,
                label           = { Text("Tipo de cultivo") },
                isError         = uiState.cultivoError != null,
                supportingText  = uiState.cultivoError?.let { { Text(it) } },
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true
            )

            // ── Hectáreas ────────────────────────────────────────────────────
            OutlinedTextField(
                value           = uiState.hectareas,
                onValueChange   = viewModel::onHectareasChange,
                label           = { Text("Hectáreas") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError         = uiState.hectareasError != null,
                supportingText  = uiState.hectareasError?.let { { Text(it) } },
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true
            )

            // ── Fecha de siembra ─────────────────────────────────────────────
            OutlinedTextField(
                value           = uiState.fechaSiembra,
                onValueChange   = viewModel::onFechaSiembraChange,
                label           = { Text("Fecha de siembra (yyyy-MM-dd)") },
                placeholder     = { Text("2026-04-15") },
                isError         = uiState.fechaSiembraError != null,
                supportingText  = uiState.fechaSiembraError?.let { { Text(it) } },
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true
            )

            // ── Riego activo ─────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text  = "Riego activo",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked         = uiState.riegoActivo,
                    onCheckedChange = viewModel::onRiegoActivoChange
                )
            }

            // ── Error general ────────────────────────────────────────────────
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
            }

            Spacer(Modifier.height(8.dp))

            // ── Botón Guardar ────────────────────────────────────────────────
            Button(
                onClick  = { viewModel.save(parcelaId) },
                enabled  = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (parcelaId == null) "Crear parcela" else "Guardar cambios")
                }
            }
        }
    }
}
