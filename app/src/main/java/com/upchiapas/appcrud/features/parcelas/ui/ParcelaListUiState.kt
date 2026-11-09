package com.upchiapas.appcrud.features.parcelas.ui

import com.upchiapas.appcrud.features.parcelas.domain.model.Parcela

/** Estado inmutable de la pantalla de lista de parcelas (Home) */
data class ParcelaListUiState(
    val isLoading: Boolean = false,
    val parcelas: List<Parcela> = emptyList(),
    val errorMessage: String? = null,
    val userMessage: String? = null,          // para Snackbar
    val showDeleteDialog: Boolean = false,
    val parcelaToDelete: Parcela? = null      // la parcela pendiente de eliminar
)
