package com.upchiapas.appcrud.features.parcelas.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO de respuesta del servidor — incluye el id asignado por json-server.
 * Todos los campos son nullable porque la API podría omitirlos (ej. al crear).
 */
@Serializable
data class ParcelaDto(
    @SerialName("id")           val id: Int?      = null,
    @SerialName("nombre")       val nombre: String? = null,
    @SerialName("cultivo")      val cultivo: String? = null,
    @SerialName("hectareas")    val hectareas: Double? = null,
    @SerialName("fechaSiembra") val fechaSiembra: String? = null,
    @SerialName("riegoActivo")  val riegoActivo: Boolean? = null
)
