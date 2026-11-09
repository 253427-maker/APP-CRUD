package com.upchiapas.appcrud.features.parcelas.data

import com.upchiapas.appcrud.core.result.mapThrowable
import com.upchiapas.appcrud.features.parcelas.data.mapper.toDomain
import com.upchiapas.appcrud.features.parcelas.data.mapper.toDomainList
import com.upchiapas.appcrud.features.parcelas.data.mapper.toRequestDto
import com.upchiapas.appcrud.features.parcelas.domain.model.Parcela
import com.upchiapas.appcrud.features.parcelas.domain.repository.ParcelaRepository

/**
 * Implementación del repositorio de parcelas.
 * Usa el mapper para que los DTOs nunca salgan de esta capa.
 */
class ParcelaRepositoryImpl(
    private val api: ParcelaApiService
) : ParcelaRepository {

    override suspend fun getParcelas(): Result<List<Parcela>> {
        return runCatching {
            api.getParcelas().toDomainList()
        }.mapThrowable()
    }

    override suspend fun getParcelaById(id: Int): Result<Parcela> {
        return runCatching {
            api.getParcelaById(id).toDomain()
        }.mapThrowable()
    }

    override suspend fun createParcela(parcela: Parcela): Result<Parcela> {
        return runCatching {
            api.createParcela(parcela.toRequestDto()).toDomain()
        }.mapThrowable()
    }

    override suspend fun updateParcela(parcela: Parcela): Result<Parcela> {
        return runCatching {
            api.updateParcela(parcela.id, parcela.toRequestDto()).toDomain()
        }.mapThrowable()
    }

    override suspend fun deleteParcela(id: Int): Result<Unit> {
        return runCatching {
            api.deleteParcela(id)
        }.mapThrowable()
    }
}
