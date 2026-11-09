package com.upchiapas.appcrud.features.auth.data.mapper

import com.upchiapas.appcrud.features.auth.data.dto.LoginResponseDto
import com.upchiapas.appcrud.features.auth.domain.model.Session
import com.upchiapas.appcrud.features.auth.domain.model.User

/**
 * Convierte el DTO de respuesta del servidor al modelo de dominio Session.
 * Solo los repositorios usan este mapper — los DTOs nunca llegan al ViewModel.
 */
fun LoginResponseDto.toDomain(): Session {
    return Session(
        accessToken = accessToken,
        user = User(
            id    = user.id,
            name  = user.name,
            email = user.email
        )
    )
}
