package com.upchiapas.appcrud

import android.app.Application

/**
 * Clase Application de la app.
 * Crea el AppContainer UNA SOLA VEZ (singleton) y lo expone a toda la app.
 * Las Activities y Composables acceden a él mediante:
 *   (application as MyApp).container
 */
class MyApp : Application() {

    // Se inicializa la primera vez que alguien accede a él
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
