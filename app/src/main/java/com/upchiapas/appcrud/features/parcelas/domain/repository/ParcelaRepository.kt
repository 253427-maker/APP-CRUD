package com.upchiapas.appcrud.features.parcelas.domain.repository

import com.upchiapas.appcrud.features.parcelas.domain.model.Parcela

/**
 * Contrato del repositorio de parcelas.
 * El ViewModel depende de ESTA interfaz, no de la implementación.
 */
interface ParcelaRepository {
    suspend fun getParcelas(): Result<List<Parcela>>
    suspend fun getParcelaById(id: Int): Result<Parcela>
    suspend fun createParcela(parcela: Parcela): Result<Parcela>
    suspend fun updateParcela(parcela: Parcela): Result<Parcela>
    suspend fun deleteParcela(id: Int): Result<Unit>
}
