package com.upchiapas.appcrud.features.parcelas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.upchiapas.appcrud.features.auth.domain.SessionStorage
import com.upchiapas.appcrud.features.parcelas.domain.model.Parcela
import com.upchiapas.appcrud.features.parcelas.domain.repository.ParcelaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de la lista de parcelas.
 * Maneja carga, eliminación con diálogo de confirmación y cierre de sesión.
 */
class ParcelaListViewModel(
    private val parcelaRepository: ParcelaRepository,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParcelaListUiState())
    val uiState: StateFlow<ParcelaListUiState> = _uiState.asStateFlow()

    init {
        loadParcelas()
    }

    // ── Cargar lista ─────────────────────────────────────────────────────────
    fun loadParcelas() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            parcelaRepository.getParcelas().fold(
                onSuccess = { list ->
                    _uiState.update { it.copy(isLoading = false, parcelas = list) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading    = false,
                            errorMessage = error.message ?: "Error al cargar las parcelas"
                        )
                    }
                }
            )
        }
    }

    // ── Eliminar ─────────────────────────────────────────────────────────────
    fun onDeleteClick(parcela: Parcela) {
        _uiState.update { it.copy(showDeleteDialog = true, parcelaToDelete = parcela) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false, parcelaToDelete = null) }
    }

    fun confirmDelete() {
        val parcela = _uiState.value.parcelaToDelete ?: return
        _uiState.update { it.copy(showDeleteDialog = false, parcelaToDelete = null) }
        viewModelScope.launch {
            parcelaRepository.deleteParcela(parcela.id).fold(
                onSuccess = {
                    // Actualiza la lista localmente sin recargar del servidor
                    _uiState.update { state ->
                        state.copy(
                            parcelas    = state.parcelas.filter { it.id != parcela.id },
                            userMessage = "\"${parcela.nombre}\" eliminada"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(userMessage = error.message ?: "Error al eliminar") }
                }
            )
        }
    }

    // ── Sesión ───────────────────────────────────────────────────────────────
    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            sessionStorage.clearToken()
            onDone()
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    companion object {
        fun factory(
            parcelaRepository: ParcelaRepository,
            sessionStorage: SessionStorage
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { ParcelaListViewModel(parcelaRepository, sessionStorage) }
        }
    }
}
