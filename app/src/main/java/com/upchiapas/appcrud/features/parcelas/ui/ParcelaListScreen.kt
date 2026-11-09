package com.upchiapas.appcrud.features.parcelas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upchiapas.appcrud.features.parcelas.domain.model.Parcela

/**
 * Pantalla Home — lista de parcelas.
 * RF-05: LazyColumn con estados carga / vacío / error + botón Reintentar.
 * RF-09: AlertDialog de confirmación antes de eliminar.
 * RF-04: botón de cerrar sesión.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelaListScreen(
    viewModel: ParcelaListViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Recarga la lista fresca cada vez que se ingresa a la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadParcelas()
    }

    // Muestra Snackbar cuando hay mensaje de usuario
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Parcelas") },
                actions = {
                    IconButton(onClick = { viewModel.logout(onLogout) }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Nueva parcela")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                // ── Cargando ────────────────────────────────────────────────
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                // ── Error ───────────────────────────────────────────────────
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text  = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = viewModel::loadParcelas) {
                            Text("Reintentar")
                        }
                    }
                }

                // ── Lista vacía ─────────────────────────────────────────────
                uiState.parcelas.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No hay parcelas registradas")
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = onNavigateToCreate) {
                            Text("Agregar primera parcela")
                        }
                    }
                }

                // ── Lista con datos ─────────────────────────────────────────
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.parcelas, key = { it.id }) { parcela ->
                            ParcelaCard(
                                parcela         = parcela,
                                onDetailClick   = { onNavigateToDetail(parcela.id) },
                                onEditClick     = { onNavigateToEdit(parcela.id) },
                                onDeleteClick   = { viewModel.onDeleteClick(parcela) }
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Diálogo de confirmación de eliminación (RF-09) ───────────────────────
    if (uiState.showDeleteDialog && uiState.parcelaToDelete != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteDialog,
            title   = { Text("Eliminar parcela") },
            text    = { Text("¿Estás seguro de eliminar \"${uiState.parcelaToDelete!!.nombre}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = viewModel::confirmDelete,
                    colors  = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeleteDialog) { Text("Cancelar") }
            }
        )
    }
}

/** Tarjeta individual de parcela */
@Composable
private fun ParcelaCard(
    parcela: Parcela,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick  = onDetailClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = parcela.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    text  = "${parcela.cultivo} · ${parcela.hectareas} ha",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text  = if (parcela.riegoActivo) "💧 Riego activo" else "🌵 Sin riego",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
