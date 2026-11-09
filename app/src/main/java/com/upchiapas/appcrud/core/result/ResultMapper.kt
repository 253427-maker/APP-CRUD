package com.upchiapas.appcrud.core.result

import retrofit2.HttpException
import java.io.IOException

/**
 * Extensión de Result que convierte excepciones técnicas en mensajes legibles.
 * Reutilizable por cualquier repositorio de la app.
 *
 * Patrón tomado del repo de referencia (PostsRepositoryImpl.mapFailure),
 * extraído a core/ para cumplir DRY.
 */
fun <T> Result<T>.mapThrowable(): Result<T> {
    return fold(
        onSuccess = { Result.success(it) },
        onFailure = { throwable ->
            Result.failure(
                when (throwable) {
                    is HttpException -> Exception(
                        "Error del servidor (${throwable.code()}): ${throwable.message()}"
                    )
                    is IOException -> Exception(
                        "Sin conexión. Verifica tu red e inténtalo de nuevo."
                    )
                    else -> throwable
                }
            )
        }
    )
}
