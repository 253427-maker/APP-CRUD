package com.upchiapas.appcrud.features.parcelas.ui

import com.upchiapas.appcrud.features.parcelas.domain.model.Parcela

/** Estado inmutable del formulario crear/editar parcela */
data class ParcelaFormUiState(
    // Campos del formulario (guardados en ViewModel → sobreviven a la rotación)
    val nombre: String = "",
    val cultivo: String = "",
    val hectareas: String = "",           // String para el campo de texto
    val fechaSiembra: String = "",        // yyyy-MM-dd
    val riegoActivo: Boolean = false,

    // Errores de validación
    val nombreError: String? = null,
    val cultivoError: String? = null,
    val hectareasError: String? = null,
    val fechaSiembraError: String? = null,

    // Control de flujo
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false,     // dispara navegación de regreso
    val isEditMode: Boolean = false       // true = editar, false = crear
)
