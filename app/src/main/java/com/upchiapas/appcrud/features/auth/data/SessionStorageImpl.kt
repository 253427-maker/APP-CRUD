package com.upchiapas.appcrud.features.auth.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.upchiapas.appcrud.features.auth.domain.SessionStorage
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

// Extensión de Context para acceder al DataStore — se crea una sola instancia por proceso
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

/**
 * Implementación de SessionStorage usando DataStore.
 * Guarda el token como String en disco de forma asíncrona.
 */
class SessionStorageImpl(private val context: Context) : SessionStorage {

    private val tokenKey = stringPreferencesKey("access_token")

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[tokenKey] = token
        }
    }

    override suspend fun getToken(): String? {
        return context.dataStore.data
            .map { prefs -> prefs[tokenKey] }
            .firstOrNull()
    }

    override suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(tokenKey)
        }
    }
}
