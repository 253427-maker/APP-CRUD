package com.upchiapas.appcrud.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** DTO para el body del POST /register */
@Serializable
data class RegisterRequestDto(
    @SerialName("name")     val name: String,
    @SerialName("email")    val email: String,
    @SerialName("password") val password: String
)
