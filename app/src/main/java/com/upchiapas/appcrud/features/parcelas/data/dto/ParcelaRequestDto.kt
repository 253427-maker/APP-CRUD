package com.upchiapas.appcrud.features.parcelas.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para crear o editar una parcela (body del POST / PUT).
 * No incluye id porque el servidor lo asigna en creación.
 */
@Serializable
data class ParcelaRequestDto(
    @SerialName("nombre")       val nombre: String,
    @SerialName("cultivo")      val cultivo: String,
    @SerialName("hectareas")    val hectareas: Double,
    @SerialName("fechaSiembra") val fechaSiembra: String,
    @SerialName("riegoActivo")  val riegoActivo: Boolean
)
