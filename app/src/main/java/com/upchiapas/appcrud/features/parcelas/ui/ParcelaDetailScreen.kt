package com.upchiapas.appcrud.features.parcelas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.upchiapas.appcrud.features.parcelas.domain.model.Parcela

/**
 * Pantalla de detalle — RF-06.
 * Muestra TODOS los atributos de la parcela.
 * No necesita ViewModel propio; recibe la parcela del NavHost que la cargó.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelaDetailScreen(
    parcela: Parcela,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de parcela") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(parcela.id) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DetalleItem("Nombre",         parcela.nombre)
            DetalleItem("Cultivo",        parcela.cultivo)
            DetalleItem("Hectáreas",      "${parcela.hectareas} ha")
            DetalleItem("Fecha de siembra", parcela.fechaSiembra)
            DetalleItem("Riego activo",   if (parcela.riegoActivo) "Sí ✅" else "No ❌")
            DetalleItem("ID interno",     "#${parcela.id}")
        }
    }
}

@Composable
private fun DetalleItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}
