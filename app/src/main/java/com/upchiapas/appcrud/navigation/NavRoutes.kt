package com.upchiapas.appcrud.navigation

/** Rutas de navegación — cada pantalla tiene su ruta única */
object NavRoutes {
    const val LOGIN    = "login"
    const val REGISTER = "register"
    const val HOME     = "home"
    const val DETAIL   = "detail/{parcelaId}"
    const val CREATE   = "form"
    const val EDIT     = "form/{parcelaId}"

    /** Genera la ruta real para navegar al detalle */
    fun detail(id: Int) = "detail/$id"

    /** Genera la ruta real para navegar al editor */
    fun edit(id: Int) = "form/$id"
}
