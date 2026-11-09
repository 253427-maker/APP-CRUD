package com.upchiapas.appcrud.features.parcelas.data

import com.upchiapas.appcrud.features.parcelas.data.dto.ParcelaDto
import com.upchiapas.appcrud.features.parcelas.data.dto.ParcelaRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/** Interface Retrofit para los endpoints de parcelas (todos requieren token via interceptor) */
interface ParcelaApiService {

    @GET("parcelas")
    suspend fun getParcelas(): List<ParcelaDto>

    @GET("parcelas/{id}")
    suspend fun getParcelaById(@Path("id") id: Int): ParcelaDto

    @POST("parcelas")
    suspend fun createParcela(@Body parcela: ParcelaRequestDto): ParcelaDto

    @PUT("parcelas/{id}")
    suspend fun updateParcela(
        @Path("id") id: Int,
        @Body parcela: ParcelaRequestDto
    ): ParcelaDto

    @DELETE("parcelas/{id}")
    suspend fun deleteParcela(@Path("id") id: Int)
}
