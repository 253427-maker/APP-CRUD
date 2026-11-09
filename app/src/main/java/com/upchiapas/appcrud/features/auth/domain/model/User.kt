package com.upchiapas.appcrud.features.auth.domain.model

/** Modelo de dominio — representa al usuario autenticado */
data class User(
    val id: Int,
    val name: String,
    val email: String
)
