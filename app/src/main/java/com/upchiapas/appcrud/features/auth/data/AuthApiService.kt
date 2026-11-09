package com.upchiapas.appcrud.features.auth.data

import com.upchiapas.appcrud.features.auth.data.dto.LoginRequestDto
import com.upchiapas.appcrud.features.auth.data.dto.LoginResponseDto
import com.upchiapas.appcrud.features.auth.data.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

/** Interface Retrofit para los endpoints de autenticación */
interface AuthApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("register")
    suspend fun register(@Body request: RegisterRequestDto): LoginResponseDto
}
