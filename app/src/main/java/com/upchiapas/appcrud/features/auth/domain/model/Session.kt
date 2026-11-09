package com.upchiapas.appcrud.features.auth.domain.model

/** Modelo de dominio — datos de sesión activa (el token que guardamos) */
data class Session(
    val accessToken: String,
    val user: User
)
