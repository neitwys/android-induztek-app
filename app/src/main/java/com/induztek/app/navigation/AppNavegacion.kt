package com.induztek.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.induztek.app.data.InduztekDbHelper
import com.induztek.app.data.SesionPreferencias
import com.induztek.app.model.Usuario
import com.induztek.app.screens.ClientesScreen
import com.induztek.app.screens.HistorialEquiposScreen
import com.induztek.app.screens.LoginScreen
import com.induztek.app.screens.MenuPrincipalScreen
import com.induztek.app.screens.PerfilScreen
import com.induztek.app.screens.RecuperarContrasenaScreen
import com.induztek.app.screens.RegistroEmpresaScreen
import com.induztek.app.screens.RegistroPruebaScreen
import com.induztek.app.screens.RegistroUsuarioEmpresaScreen
import com.induztek.app.ui.theme.ModoTema

@Composable
fun AppNavegacion(
    modoTemaActual: ModoTema = ModoTema.SISTEMA,
    onCambiarTema: (ModoTema) -> Unit = {}
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sesionPrefs = remember { SesionPreferencias(context) }
    val dbHelper = remember { InduztekDbHelper(context) }

    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }

    val idUsuarioGuardado = sesionPrefs.leer()
    val destinoInicial = if (idUsuarioGuardado != -1L) {
        usuarioActual = dbHelper.buscarPorId(idUsuarioGuardado)
        if (usuarioActual != null) Ruta.MenuPrincipal.ruta else Ruta.Login.ruta
    } else {
        Ruta.Login.ruta
    }

    NavHost(
        navController = navController,
        startDestination = destinoInicial
    ) {
        // 1. Login
        composable(Ruta.Login.ruta) {
            LoginScreen(
                onLoginExitoso = { usuario ->
                    usuarioActual = usuario
                    sesionPrefs.guardar(usuario.id) // <-- Usa guardar(id)
                    navController.navigate(Ruta.MenuPrincipal.ruta) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                },
                onNavegarRecuperarContrasena = {
                    navController.navigate(Ruta.RecuperarContrasena.ruta)
                }
            )
        }

        // 2. Recuperar Contraseña
        composable(Ruta.RecuperarContrasena.ruta) {
            RecuperarContrasenaScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        // 3. Menú Principal
        composable(Ruta.MenuPrincipal.ruta) {
            MenuPrincipalScreen(
                usuario = usuarioActual,
                modoTemaActual = modoTemaActual,
                onCambiarTema = onCambiarTema,
                onNavegarPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                onNavegarRegistroCliente = { navController.navigate(Ruta.RegistroEmpresa.ruta) },
                onNavegarListaClientes = { navController.navigate(Ruta.Clientes.ruta) },
                onNavegarRegistroPrueba = { navController.navigate(Ruta.RegistroPrueba.ruta) },
                onNavegarHistorialEquipos = { navController.navigate(Ruta.HistorialEquipos.ruta) },
                onCerrarSesion = {
                    sesionPrefs.borrar()
                    usuarioActual = null
                    navController.navigate(Ruta.Login.ruta) {
                        popUpTo(Ruta.MenuPrincipal.ruta) { inclusive = true }
                    }
                }
            )
        }

        // 4. Registro Empresa
        composable(Ruta.RegistroEmpresa.ruta) {
            RegistroEmpresaScreen(
                onNavegarARegistroUsuario = { idGenerado ->
                    navController.navigate(Ruta.RegistroUsuarioEmpresa.crearRuta(idGenerado)) {
                        popUpTo(Ruta.RegistroEmpresa.ruta) { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        // 4.1 Registro Representante de la Empresa
        composable(
            route = Ruta.RegistroUsuarioEmpresa.ruta,
            arguments = listOf(navArgument("empresaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val idEmpresa = backStackEntry.arguments?.getLong("empresaId") ?: 0L

            RegistroUsuarioEmpresaScreen(
                empresaId = idEmpresa,
                onVolver = { navController.popBackStack() }
            )
        }

        // 5. Lista Clientes
        composable(Ruta.Clientes.ruta) {
            ClientesScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        // 6. Registro de Prueba en Terreno
        composable(Ruta.RegistroPrueba.ruta) {
            RegistroPruebaScreen(
                idUsuarioTecnico = usuarioActual?.id ?: 1L,
                onVolver = { navController.popBackStack() }
            )
        }

        // 7. Historial y Consulta de Equipos
        composable(Ruta.HistorialEquipos.ruta) {
            HistorialEquiposScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        // 8. Perfil de Usuario
        composable(Ruta.Perfil.ruta) {
            PerfilScreen(
                usuario = usuarioActual,
                onVolver = { navController.popBackStack() },
                onCerrarSesion = {
                    sesionPrefs.borrar()
                    usuarioActual = null
                    navController.navigate(Ruta.Login.ruta) {
                        popUpTo(Ruta.MenuPrincipal.ruta) { inclusive = true }
                    }
                }
            )
        }
    }
}