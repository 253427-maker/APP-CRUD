package com.upchiapas.appcrud

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.upchiapas.appcrud.core.network.AuthInterceptor
import com.upchiapas.appcrud.features.auth.data.AuthApiService
import com.upchiapas.appcrud.features.auth.data.AuthRepositoryImpl
import com.upchiapas.appcrud.features.auth.data.SessionStorageImpl
import com.upchiapas.appcrud.features.auth.domain.SessionStorage
import com.upchiapas.appcrud.features.auth.domain.repository.AuthRepository
import com.upchiapas.appcrud.features.auth.ui.LoginViewModel
import com.upchiapas.appcrud.features.auth.ui.RegisterViewModel
import com.upchiapas.appcrud.features.parcelas.data.ParcelaApiService
import com.upchiapas.appcrud.features.parcelas.data.ParcelaRepositoryImpl
import com.upchiapas.appcrud.features.parcelas.domain.repository.ParcelaRepository
import com.upchiapas.appcrud.features.parcelas.ui.ParcelaFormViewModel
import com.upchiapas.appcrud.features.parcelas.ui.ParcelaListViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

/**
 * Contenedor de dependencias — creado UNA SOLA VEZ en MyApp.
 *
 * Orden de construcción:
 *   SessionStorage → AuthInterceptor → OkHttp → Retrofit
 *   → AuthApi + ParcelaApi → Repositorios → Factories de ViewModels
 *
 * Todas las dependencias se tipan como INTERFACES para desacoplar.
 */
class AppContainer(context: Context) {

    // ── URL base (emulador → 10.0.2.2 apunta a localhost del PC) ───────────
    private val baseUrl = "http://10.0.2.2:3000/"

    // ── Sesión ──────────────────────────────────────────────────────────────
    val sessionStorage: SessionStorage = SessionStorageImpl(context)

    // ── Red ─────────────────────────────────────────────────────────────────
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = AuthInterceptor(sessionStorage)

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)        // primero auth, luego logging
        .addInterceptor(loggingInterceptor)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true  // si el servidor agrega campos nuevos, no falla
        isLenient = true
    }

    // UNA SOLA instancia de Retrofit compartida por toda la app
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    // ── APIs ────────────────────────────────────────────────────────────────
    private val authApi: AuthApiService =
        retrofit.create(AuthApiService::class.java)

    private val parcelaApi: ParcelaApiService =
        retrofit.create(ParcelaApiService::class.java)

    // ── Repositorios (singletons, tipados como interfaces) ──────────────────
    val authRepository: AuthRepository =
        AuthRepositoryImpl(authApi, sessionStorage)

    val parcelaRepository: ParcelaRepository =
        ParcelaRepositoryImpl(parcelaApi)

    // ── Factories de ViewModels ─────────────────────────────────────────────
    val loginViewModelFactory = LoginViewModel.factory(authRepository, sessionStorage)
    val registerViewModelFactory = RegisterViewModel.factory(authRepository)
    val parcelaListViewModelFactory = ParcelaListViewModel.factory(parcelaRepository, sessionStorage)
    val parcelaFormViewModelFactory = ParcelaFormViewModel.factory(parcelaRepository)
}
