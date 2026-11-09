package com.upchiapas.appcrud.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** DTO para el body del POST /login */
@Serializable
data class LoginRequestDto(
    @SerialName("email")    val email: String,
    @SerialName("password") val password: String
)
