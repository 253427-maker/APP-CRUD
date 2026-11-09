package com.upchiapas.appcrud.features.auth.data

import com.upchiapas.appcrud.core.result.mapThrowable
import com.upchiapas.appcrud.features.auth.data.dto.LoginRequestDto
import com.upchiapas.appcrud.features.auth.data.dto.RegisterRequestDto
import com.upchiapas.appcrud.features.auth.data.mapper.toDomain
import com.upchiapas.appcrud.features.auth.domain.SessionStorage
import com.upchiapas.appcrud.features.auth.domain.model.Session
import com.upchiapas.appcrud.features.auth.domain.repository.AuthRepository

/**
 * Implementación concreta de AuthRepository.
 * Llama al API, usa el Mapper para convertir DTO → dominio,
 * y guarda el token en SessionStorage.
 */
class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val sessionStorage: SessionStorage
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Session> {
        return runCatching {
            val dto = api.login(LoginRequestDto(email = email, password = password))
            val session = dto.toDomain()
            sessionStorage.saveToken(session.accessToken)   // persiste el token
            session
        }.mapThrowable()
    }

    override suspend fun register(name: String, email: String, password: String): Result<Session> {
        return runCatching {
            val dto = api.register(RegisterRequestDto(name = name, email = email, password = password))
            val session = dto.toDomain()
            sessionStorage.saveToken(session.accessToken)
            session
        }.mapThrowable()
    }
}
