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
import com.induztek.app.screens.ClientesScreen
import com.induztek.app.screens.HistorialEquiposScreen
import com.induztek.app.screens.LoginScreen
import com.induztek.app.screens.MenuPrincipalScreen
import com.induztek.app.screens.RegistroClienteScreen
import com.induztek.app.screens.RegistroPruebaScreen
import com.induztek.app.ui.theme.ModoTema

@Composable
fun AppNavegacion(
    modoTemaActual: ModoTema = ModoTema.SISTEMA,
    onCambiarTema: (ModoTema) -> Unit = {}
) {
    val navController = rememberNavController()
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }

    NavHost(
        navController = navController,
        startDestination = Ruta.Login.ruta
    ) {
        // 1. Login
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

        // 2. Menú Principal
        composable(Ruta.MenuPrincipal.ruta) {
            MenuPrincipalScreen(
                usuario = usuarioActual,
                modoTemaActual = modoTemaActual,
                onCambiarTema = onCambiarTema,
                onNavegarRegistroCliente = {
                    navController.navigate(Ruta.RegistroCliente.ruta)
                },
                onNavegarListaClientes = {
                    navController.navigate(Ruta.Clientes.ruta)
                },
                onNavegarRegistroPrueba = {
                    navController.navigate(Ruta.RegistroPrueba.ruta)
                },
                onNavegarHistorialEquipos = {
                    navController.navigate(Ruta.HistorialEquipos.ruta)
                },
                onCerrarSesion = {
                    val sesionPrefs = com.induztek.app.data.SesionPreferencias(navController.context)
                    sesionPrefs.borrar()
                    usuarioActual = null
                    navController.navigate(Ruta.Login.ruta) {
                        popUpTo(Ruta.MenuPrincipal.ruta) { inclusive = true }
                    }
                }
            )
        }

        // 3. Registro Clientes
        composable(Ruta.RegistroCliente.ruta) {
            RegistroClienteScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        // 4. Lista Clientes
        composable(Ruta.Clientes.ruta) {
            ClientesScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        // 5. NUEVA: Registro de Prueba en Terreno
        composable(Ruta.RegistroPrueba.ruta) {
            RegistroPruebaScreen(
                idUsuarioTecnico = usuarioActual?.id ?: 1L,
                onVolver = { navController.popBackStack() }
            )
        }

        // 6. NUEVA: Historial y Consulta de Equipos
        composable(Ruta.HistorialEquipos.ruta) {
            HistorialEquiposScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}