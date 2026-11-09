package com.upchiapas.appcrud.core.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor de resiliencia para el entorno de desarrollo con emulador.
 * Si la conexión a 10.0.2.2 falla (por problemas del router virtual de QEMU o Firewall),
 * reintenta automáticamente usando 127.0.0.1 (soportado vía adb reverse).
 */
class HostFallbackInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return try {
            chain.proceed(request)
        } catch (e: IOException) {
            val originalHost = request.url.host
            if (originalHost == "10.0.2.2") {
                val newUrl = request.url.newBuilder().host("127.0.0.1").build()
                val newRequest = request.newBuilder().url(newUrl).build()
                chain.proceed(newRequest)
            } else {
                throw e
            }
        }
    }
}
