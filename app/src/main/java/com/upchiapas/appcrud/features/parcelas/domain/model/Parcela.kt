package com.upchiapas.appcrud.features.parcelas.domain.model

/**
 * Modelo de dominio — Parcela.
 * Nunca sale de la capa domain hacia data (solo los DTOs hacen eso).
 *
 * Validaciones definidas en el ViewModel:
 *   - nombre: obligatorio, mín. 3 caracteres
 *   - cultivo: obligatorio
 *   - hectareas: > 0
 *   - fechaSiembra: formato yyyy-MM-dd
 */
data class Parcela(
    val id: Int,
    val nombre: String,
    val cultivo: String,
    val hectareas: Double,
    val fechaSiembra: String,   // yyyy-MM-dd
    val riegoActivo: Boolean
)
