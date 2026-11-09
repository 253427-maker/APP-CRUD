package com.upchiapas.appcrud.features.auth.domain.repository

import com.upchiapas.appcrud.features.auth.domain.model.Session

/**
 * Contrato de autenticación — el ViewModel depende de ESTA interfaz, nunca de la impl.
 * Ambas funciones son suspend porque hacen llamadas de red.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Session>
    suspend fun register(name: String, email: String, password: String): Result<Session>
}
