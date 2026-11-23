package com.induztek.app.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.induztek.app.data.SesionPreferencias
import com.induztek.app.data.repository.RemoteUsuarioRepository
import com.induztek.app.model.Usuario
import com.induztek.app.screens.CrearContrasenaScreen
import com.induztek.app.screens.HistorialEquiposScreen
import com.induztek.app.screens.ListaYEliminarEmpresasScreen
import com.induztek.app.screens.ListaYEliminarUsuariosScreen
import com.induztek.app.screens.LoginScreen
import com.induztek.app.screens.MenuPrincipalScreen
import com.induztek.app.screens.PerfilScreen
import com.induztek.app.screens.RecuperarContrasenaScreen
import com.induztek.app.screens.RegistroEmpresaScreen
import com.induztek.app.screens.RegistroPruebaScreen
import com.induztek.app.screens.RegistroUsuarioEmpresaScreen
import com.induztek.app.ui.theme.ModoTema
import kotlinx.coroutines.launch

@Composable
fun AppNavegacion(
    modoTemaActual: ModoTema = ModoTema.CLARO,
    onCambiarTema: (ModoTema) -> Unit = {}
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sesionPrefs = remember { SesionPreferencias(context) }
    val usuarioRepo = remember { RemoteUsuarioRepository() }
    val scope = rememberCoroutineScope()

    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }
    val idUsuarioGuardado = sesionPrefs.leer()

    LaunchedEffect(idUsuarioGuardado) {
        if (idUsuarioGuardado != -1L && usuarioActual == null) {
            try {
                val usuario = usuarioRepo.buscarPorId(idUsuarioGuardado)
                usuarioActual = usuario
                val destino = if (usuario.requiereCambioClave) {
                    Ruta.CrearContrasena.crearRuta(usuario.id)
                } else {
                    Ruta.MenuPrincipal.ruta
                }
                navController.navigate(destino) {
                    popUpTo(Ruta.Login.ruta) { inclusive = true }
                }
            } catch (_: Exception) {
                sesionPrefs.borrar()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Ruta.Login.ruta,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { 300 }, animationSpec = tween(250)) + fadeIn(tween(250))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -300 }, animationSpec = tween(250)) + fadeOut(tween(250))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -300 }, animationSpec = tween(250)) + fadeIn(tween(250))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { 300 }, animationSpec = tween(250)) + fadeOut(tween(250))
        }
    ) {
        // 1. Login
        composable(Ruta.Login.ruta) {
            LoginScreen(
                onLoginExitoso = { usuario ->
                    usuarioActual = usuario
                    sesionPrefs.guardar(usuario.id)

                    if (usuario.requiereCambioClave) {
                        navController.navigate(Ruta.CrearContrasena.crearRuta(usuario.id)) {
                            popUpTo(Ruta.Login.ruta) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Ruta.MenuPrincipal.ruta) {
                            popUpTo(Ruta.Login.ruta) { inclusive = true }
                        }
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

        // 2.5 Crear PRIMERA Contraseña
        composable(
            route = Ruta.CrearContrasena.ruta,
            arguments = listOf(navArgument("idUsuario") { type = NavType.LongType })
        ) { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getLong("idUsuario") ?: 0L

            CrearContrasenaScreen(
                idUsuario = idUsuario,
                alNavegarAlInicio = {
                    sesionPrefs.borrar()
                    usuarioActual = null
                    navController.navigate(Ruta.Login.ruta) {
                        popUpTo(0) { inclusive = true }
                    }
                }
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
                onNavegarRegistroUsuarioInterno = {
                    navController.navigate(Ruta.RegistroUsuarioEmpresa.crearRuta(0L))
                },
                onNavegarListaYEliminarEmpresas = { navController.navigate(Ruta.ListaYEliminarEmpresas.ruta) },
                onNavegarRegistroPrueba = { navController.navigate(Ruta.RegistroPrueba.ruta) },
                onNavegarHistorialEquipos = { navController.navigate(Ruta.HistorialEquipos.ruta) },
                onNavegarMiEmpresa = { navController.navigate(Ruta.MiEmpresa.ruta) },
                onNavegarListaUsuarios = {
                    navController.navigate("lista_usuarios")
                },
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

        // 4.1 Registro de Usuario (Representante si empresaId > 0, o Personal Interno si empresaId == 0)
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

        // 5. Lista y eliminar Empresas
        composable(Ruta.ListaYEliminarEmpresas.ruta) {
            ListaYEliminarEmpresasScreen(
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

        // 7. Historial y Consulta de Equipos (filtrado por empresa si rolId == 4)
        composable(Ruta.HistorialEquipos.ruta) {
            val filtroEmpresa = if (usuarioActual?.rolId == 4L) usuarioActual?.empresaId else null
            HistorialEquiposScreen(
                empresaIdFiltro = filtroEmpresa,
                onVolver = { navController.popBackStack() }
            )
        }

        // 8. Perfil de Usuario
        composable(Ruta.Perfil.ruta) {
            PerfilScreen(
                usuario = usuarioActual,
                onVolver = {
                    usuarioActual?.id?.let { id ->
                        scope.launch {
                            try {
                                usuarioActual = usuarioRepo.buscarPorId(id)
                            } catch (_: Exception) {
                            }
                        }
                    }
                    navController.popBackStack()
                },
                onCerrarSesion = {
                    sesionPrefs.borrar()
                    usuarioActual = null
                    navController.navigate(Ruta.Login.ruta) {
                        popUpTo(Ruta.MenuPrincipal.ruta) { inclusive = true }
                    }
                }
            )
        }

        // 9. Listado y Gestión de Usuarios (Solo Jefe Técnico)
        composable("lista_usuarios") {
            ListaYEliminarUsuariosScreen(
                idUsuarioActual = usuarioActual?.id ?: 0L,
                onVolver = { navController.popBackStack() }
            )
        }


        // 10. Ver su propia empresa.
        composable(Ruta.MiEmpresa.ruta) {
            val idEmpresaDelUsuario = usuarioActual?.empresaId ?: 0L

            com.induztek.app.screens.MiEmpresaScreen(
                empresaId = idEmpresaDelUsuario,
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
                onVolver = { navController.popBackStack() }
            )
        }
    }
}