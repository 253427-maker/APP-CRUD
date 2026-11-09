package com.upchiapas.appcrud.features.parcelas.data.mapper

import com.upchiapas.appcrud.features.parcelas.data.dto.ParcelaDto
import com.upchiapas.appcrud.features.parcelas.data.dto.ParcelaRequestDto
import com.upchiapas.appcrud.features.parcelas.domain.model.Parcela

/**
 * Convierte DTO → modelo de dominio.
 * Usa valores seguros en caso de null para evitar crashes.
 */
fun ParcelaDto.toDomain(): Parcela {
    return Parcela(
        id           = id ?: 0,
        nombre       = nombre.orEmpty(),
        cultivo      = cultivo.orEmpty(),
        hectareas    = hectareas ?: 0.0,
        fechaSiembra = fechaSiembra.orEmpty(),
        riegoActivo  = riegoActivo ?: false
    )
}

/** Convierte lista de DTOs → lista de modelos de dominio */
fun List<ParcelaDto>.toDomainList(): List<Parcela> = map { it.toDomain() }

/**
 * Convierte modelo de dominio → DTO de solicitud (para POST/PUT).
 * No incluye id porque el servidor lo gestiona.
 */
fun Parcela.toRequestDto(): ParcelaRequestDto {
    return ParcelaRequestDto(
        nombre       = nombre,
        cultivo      = cultivo,
        hectareas    = hectareas,
        fechaSiembra = fechaSiembra,
        riegoActivo  = riegoActivo
    )
}
