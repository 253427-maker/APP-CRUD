package com.upchiapas.appcrud.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** DTO con la respuesta del servidor al hacer login/register */
@Serializable
data class LoginResponseDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("user")        val user: UserDto
)

@Serializable
data class UserDto(
    @SerialName("id")    val id: Int,
    @SerialName("name")  val name: String,
    @SerialName("email") val email: String
)
