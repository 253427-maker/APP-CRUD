package com.upchiapas.appcrud.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.upchiapas.appcrud.AppContainer
import com.upchiapas.appcrud.features.auth.ui.*
import com.upchiapas.appcrud.features.parcelas.domain.model.Parcela
import com.upchiapas.appcrud.features.parcelas.ui.*
import kotlinx.coroutines.runBlocking

/**
 * NavHost principal de la app.
 *
 * Lógica de inicio: si ya hay token guardado → ir a Home, si no → Login.
 * RF-04: Logout navega a Login limpiando el back-stack completo.
 */
@Composable
fun AppNavHost(container: AppContainer) {
    val navController = rememberNavController()

    // Decide pantalla inicial leyendo el token ANTES de que se dibuje el NavHost
    val startDestination = remember {
        val token = runBlocking { container.sessionStorage.getToken() }
        if (!token.isNullOrBlank()) NavRoutes.HOME else NavRoutes.LOGIN
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // ── Login ────────────────────────────────────────────────────────────
        composable(NavRoutes.LOGIN) {
            val vm: LoginViewModel = viewModel(factory = container.loginViewModelFactory)
            LoginScreen(
                viewModel          = vm,
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.REGISTER)
                },
                onLoginSuccess     = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ── Register ─────────────────────────────────────────────────────────
        composable(NavRoutes.REGISTER) {
            val vm: RegisterViewModel = viewModel(factory = container.registerViewModelFactory)
            RegisterScreen(
                viewModel          = vm,
                onNavigateToLogin  = { navController.navigateUp() },
                onRegisterSuccess  = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ── Home (lista de parcelas) ──────────────────────────────────────────
        composable(NavRoutes.HOME) {
            val vm: ParcelaListViewModel =
                viewModel(factory = container.parcelaListViewModelFactory)
            ParcelaListScreen(
                viewModel         = vm,
                onNavigateToCreate = { navController.navigate(NavRoutes.CREATE) },
                onNavigateToDetail = { id -> navController.navigate(NavRoutes.detail(id)) },
                onNavigateToEdit  = { id -> navController.navigate(NavRoutes.edit(id)) },
                onLogout          = {
                    // RF-04: borra sesión y limpia back-stack hasta Login inclusive
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // ── Detalle ──────────────────────────────────────────────────────────
        composable(
            route     = NavRoutes.DETAIL,
            arguments = listOf(navArgument("parcelaId") { type = NavType.IntType })
        ) { backStackEntry ->
            // Obtenemos la parcela de la lista ya cargada en el ViewModel de Home
            // para evitar una petición extra. Si no está, mostramos pantalla vacía.
            val parcelaId = backStackEntry.arguments?.getInt("parcelaId") ?: return@composable

            // Reutilizamos el ViewModel del Home (misma instancia en el back-stack)
            val homeEntry = remember(backStackEntry) {
                navController.getBackStackEntry(NavRoutes.HOME)
            }
            val listVm: ParcelaListViewModel =
                viewModel(
                    viewModelStoreOwner = homeEntry,
                    factory             = container.parcelaListViewModelFactory
                )
            val parcela = listVm.uiState.value.parcelas.find { it.id == parcelaId }

            if (parcela != null) {
                ParcelaDetailScreen(
                    parcela = parcela,
                    onBack  = { navController.navigateUp() },
                    onEdit  = { id -> navController.navigate(NavRoutes.edit(id)) }
                )
            }
        }

        // ── Crear ────────────────────────────────────────────────────────────
        composable(NavRoutes.CREATE) {
            val vm: ParcelaFormViewModel =
                viewModel(factory = container.parcelaFormViewModelFactory)
            ParcelaFormScreen(
                viewModel     = vm,
                parcelaId     = null,
                onBack        = { navController.navigateUp() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // ── Editar ───────────────────────────────────────────────────────────
        composable(
            route     = NavRoutes.EDIT,
            arguments = listOf(navArgument("parcelaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val parcelaId = backStackEntry.arguments?.getInt("parcelaId") ?: return@composable
            val vm: ParcelaFormViewModel =
                viewModel(factory = container.parcelaFormViewModelFactory)
            ParcelaFormScreen(
                viewModel     = vm,
                parcelaId     = parcelaId,
                onBack        = { navController.navigateUp() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}
