// Archivo raíz de configuración Gradle — no toques nada aquí a menos que agregues un módulo nuevo
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
