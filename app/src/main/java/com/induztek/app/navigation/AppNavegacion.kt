package com.induztek.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.induztek.app.model.Usuario
import com.induztek.app.screens.LoginScreen
import com.induztek.app.screens.MenuPrincipalScreen
import com.induztek.app.screens.RegistroClienteScreen

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }

    NavHost(
        navController = navController,
        startDestination = Ruta.Login.ruta
    ) {
        // 1. Ruta del Login.
        composable(Ruta.Login.ruta) {
            LoginScreen(
                onLoginExitoso = { usuario ->
                        usuarioActual = usuario
                    navController.navigate(Ruta.MenuPrincipal.ruta) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                }
            )
        }

        // 2. Ruta del Menu principal.
        composable(Ruta.MenuPrincipal.ruta) {
            MenuPrincipalScreen(
                usuario = usuarioActual,
                onNavegarRegistroCliente = {
                    navController.navigate(Ruta.RegistroCliente.ruta)
                }
            )
        }

        // 2. Ruta del Formulario de Registro Clientes.
        composable(Ruta.RegistroCliente.ruta) {
            RegistroClienteScreen()
        }
    }
}