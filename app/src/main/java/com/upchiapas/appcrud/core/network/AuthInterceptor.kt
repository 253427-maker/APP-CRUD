package com.upchiapas.appcrud.core.network

import com.upchiapas.appcrud.features.auth.domain.SessionStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor de OkHttp que agrega el token Bearer a TODAS las peticiones.
 * Nunca hace falta agregar la cabecera a mano en cada endpoint.
 *
 * Lee el token de forma bloqueante (runBlocking) porque OkHttp no es
 * coroutine-aware. Es seguro aquí porque el interceptor corre en un
 * hilo de red, no en el Main.
 */
class AuthInterceptor(
    private val sessionStorage: SessionStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { sessionStorage.getToken() }

        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}
