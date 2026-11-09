package com.upchiapas.appcrud.features.auth.domain

/**
 * Contrato de almacenamiento de sesión.
 * La implementación usa DataStore; el AuthInterceptor y los ViewModels
 * dependen de ESTA interfaz para no acoplarse a DataStore.
 */
interface SessionStorage {
    /** Guarda el token de acceso en almacenamiento persistente */
    suspend fun saveToken(token: String)

    /** Recupera el token guardado, o null si no hay sesión activa */
    suspend fun getToken(): String?

    /** Borra el token (cerrar sesión) */
    suspend fun clearToken()
}
