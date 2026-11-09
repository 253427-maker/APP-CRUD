package com.upchiapas.appcrud.features.parcelas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.upchiapas.appcrud.features.parcelas.domain.model.Parcela
import com.upchiapas.appcrud.features.parcelas.domain.repository.ParcelaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel reutilizable para crear Y editar una parcela.
 * Si recibe un parcelaId, carga los datos existentes (modo editar).
 * Si parcelaId == null, el formulario empieza vacío (modo crear).
 *
 * Los campos del formulario se guardan en el ViewModel → sobreviven a la rotación.
 */
class ParcelaFormViewModel(
    private val parcelaRepository: ParcelaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParcelaFormUiState())
    val uiState: StateFlow<ParcelaFormUiState> = _uiState.asStateFlow()

    // ── Cargar datos para edición ────────────────────────────────────────────
    fun loadParcela(id: Int) {
        _uiState.update { it.copy(isLoading = true, isEditMode = true) }
        viewModelScope.launch {
            parcelaRepository.getParcelaById(id).fold(
                onSuccess = { p ->
                    _uiState.update {
                        it.copy(
                            isLoading    = false,
                            nombre       = p.nombre,
                            cultivo      = p.cultivo,
                            hectareas    = p.hectareas.toString(),
                            fechaSiembra = p.fechaSiembra,
                            riegoActivo  = p.riegoActivo
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
            )
        }
    }

    // ── Actualización de campos ──────────────────────────────────────────────
    fun onNombreChange(v: String)       = _uiState.update { it.copy(nombre = v, nombreError = null) }
    fun onCultivoChange(v: String)      = _uiState.update { it.copy(cultivo = v, cultivoError = null) }
    fun onHectareasChange(v: String)    = _uiState.update { it.copy(hectareas = v, hectareasError = null) }
    fun onFechaSiembraChange(v: String) = _uiState.update { it.copy(fechaSiembra = v, fechaSiembraError = null) }
    fun onRiegoActivoChange(v: Boolean) = _uiState.update { it.copy(riegoActivo = v) }

    // ── Guardar (crear o editar) ─────────────────────────────────────────────
    fun save(parcelaId: Int?) {
        if (!validate()) return

        val s = _uiState.value
        val parcela = Parcela(
            id           = parcelaId ?: 0,
            nombre       = s.nombre.trim(),
            cultivo      = s.cultivo.trim(),
            hectareas    = s.hectareas.toDoubleOrNull() ?: 0.0,
            fechaSiembra = s.fechaSiembra.trim(),
            riegoActivo  = s.riegoActivo
        )

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = if (parcelaId == null) {
                parcelaRepository.createParcela(parcela)
            } else {
                parcelaRepository.updateParcela(parcela)
            }
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Error al guardar")
                    }
                }
            )
        }
    }

    fun onSaveHandled() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    // ── Validación ───────────────────────────────────────────────────────────
    private fun validate(): Boolean {
        val s = _uiState.value
        val nombreError = when {
            s.nombre.isBlank()      -> "El nombre es obligatorio"
            s.nombre.trim().length < 3 -> "El nombre debe tener al menos 3 caracteres"
            else -> null
        }
        val cultivoError     = if (s.cultivo.isBlank()) "El cultivo es obligatorio" else null
        val hectareasError   = when {
            s.hectareas.isBlank()                    -> "Las hectáreas son obligatorias"
            s.hectareas.toDoubleOrNull() == null     -> "Ingresa un número válido"
            (s.hectareas.toDoubleOrNull() ?: 0.0) <= 0 -> "Las hectáreas deben ser mayores a 0"
            else -> null
        }
        val fechaError = when {
            s.fechaSiembra.isBlank() -> "La fecha de siembra es obligatoria"
            !s.fechaSiembra.matches(Regex("""\d{4}-\d{2}-\d{2}""")) ->
                "Formato requerido: yyyy-MM-dd"
            else -> null
        }

        _uiState.update {
            it.copy(
                nombreError       = nombreError,
                cultivoError      = cultivoError,
                hectareasError    = hectareasError,
                fechaSiembraError = fechaError
            )
        }
        return nombreError == null && cultivoError == null &&
               hectareasError == null && fechaError == null
    }

    companion object {
        fun factory(parcelaRepository: ParcelaRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { ParcelaFormViewModel(parcelaRepository) }
            }
    }
}
