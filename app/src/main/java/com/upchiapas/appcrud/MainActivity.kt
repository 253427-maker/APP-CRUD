package com.upchiapas.appcrud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.upchiapas.appcrud.navigation.AppNavHost
import com.upchiapas.appcrud.ui.theme.AppCRUDTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Obtenemos el container desde la Application (ya creado)
        val container = (application as MyApp).container

        setContent {
            AppCRUDTheme {
                AppNavHost(container = container)
            }
        }
    }
}
