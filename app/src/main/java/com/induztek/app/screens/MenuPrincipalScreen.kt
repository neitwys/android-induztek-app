package com.induztek.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.app.R
import com.induztek.app.model.MenuPrincipalUiState
import com.induztek.app.model.Usuario
import com.induztek.app.ui.components.TarjetaSeccion
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.ui.theme.ModoTema
import com.induztek.app.viewmodel.MenuPrincipalViewModel
import kotlinx.coroutines.launch

@Composable
fun MenuPrincipalScreen(
    usuario: Usuario?,
    modoTemaActual: ModoTema = ModoTema.SISTEMA,
    onCambiarTema: (ModoTema) -> Unit = {},
    viewModel: MenuPrincipalViewModel = viewModel(),
    onNavegarPerfil: () -> Unit = {},
    onNavegarRegistroCliente: () -> Unit,
    onNavegarRegistroUsuarioInterno: () -> Unit = {},
    onNavegarListaYEliminarEmpresas: () -> Unit,
    onNavegarRegistroPrueba: () -> Unit = {},
    onNavegarHistorialEquipos: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onNavegarListaUsuarios: () -> Unit = {}
) {
    LaunchedEffect(usuario) {
        viewModel.cargarUsuario(usuario)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MenuPrincipalContent(
        uiState = uiState,
        rolId = usuario?.rolId ?: 0L,
        modoTemaActual = modoTemaActual,
        onCambiarTema = onCambiarTema,
        onNavegarPerfil = onNavegarPerfil,
        onNavegarRegistroCliente = onNavegarRegistroCliente,
        onNavegarRegistroUsuarioInterno = onNavegarRegistroUsuarioInterno,
        onNavegarListaYEliminarEmpresas = onNavegarListaYEliminarEmpresas,
        onNavegarRegistroPrueba = onNavegarRegistroPrueba,
        onNavegarHistorialEquipos = onNavegarHistorialEquipos,
        onCerrarSesion = onCerrarSesion,
        onNavegarListaUsuarios = onNavegarListaUsuarios

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPrincipalContent(
    uiState: MenuPrincipalUiState,
    rolId: Long = 1L,
    modoTemaActual: ModoTema = ModoTema.SISTEMA,
    onCambiarTema: (ModoTema) -> Unit = {},
    onNavegarPerfil: () -> Unit = {},
    onNavegarRegistroCliente: () -> Unit,
    onNavegarRegistroUsuarioInterno: () -> Unit = {},
    onNavegarListaYEliminarEmpresas: () -> Unit,
    onNavegarRegistroPrueba: () -> Unit = {},
    onNavegarHistorialEquipos: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onNavegarListaUsuarios: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val esClienteExterno = rolId == 4L
    val puedeGestionarEmpresas = rolId == 2L || rolId == 3L
    val esJefeTecnico = rolId == 3L

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimens.espacioGrande),
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                ) {
                    Text(
                        text = "Opciones",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = Dimens.espacioChico)
                    )

                    ListItem(
                        headlineContent = { Text("Mi Perfil") },
                        leadingContent = { Icon(painter = painterResource(R.drawable.ic_person), contentDescription = null) },
                        modifier = Modifier.clickable {
                            scope.launch { drawerState.close() }
                            onNavegarPerfil()
                        }
                    )

                    if (puedeGestionarEmpresas) {
                        ListItem(
                            headlineContent = { Text("Listado de Empresas") },
                            leadingContent = { Icon(painter = painterResource(R.drawable.ic_giro), contentDescription = null) },
                            modifier = Modifier.clickable {
                                scope.launch { drawerState.close() }
                                onNavegarListaYEliminarEmpresas()
                            }
                        )
                    }

                    if (esJefeTecnico) {
                        ListItem(
                            headlineContent = { Text("Listado y Gestión de Usuarios") },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_person),
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.clickable {
                                onNavegarListaUsuarios() // <-- 1. Navega primero instantáneamente
                                scope.launch { drawerState.close() } // <-- 2. Cierra el menú en segundo plano
                            }
                        )
                        ListItem(
                            headlineContent = { Text("Registrar Usuario Interno") },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_person),
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.clickable {
                                onNavegarRegistroUsuarioInterno()
                                scope.launch { drawerState.close() }
                            }
                        )
                    }


                    if (!esClienteExterno) {
                        ListItem(
                            headlineContent = { Text("Registrar Prueba en Terreno") },
                            leadingContent = { Icon(painter = painterResource(R.drawable.ic_giro), contentDescription = null) },
                            modifier = Modifier.clickable {
                                scope.launch { drawerState.close() }
                                onNavegarRegistroPrueba()
                            }
                        )
                    }

                    ListItem(
                        headlineContent = { Text("Historial de Equipos") },
                        leadingContent = { Icon(painter = painterResource(R.drawable.ic_giro), contentDescription = null) },
                        modifier = Modifier.clickable {
                            scope.launch { drawerState.close() }
                            onNavegarHistorialEquipos()
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.espacioChico))

                    Text("Apariencia", style = MaterialTheme.typography.titleMedium)

                    ListItem(
                        headlineContent = { Text("Modo Claro") },
                        leadingContent = {
                            RadioButton(
                                selected = modoTemaActual == ModoTema.CLARO,
                                onClick = null
                            )
                        },
                        modifier = Modifier.clickable { onCambiarTema(ModoTema.CLARO) }
                    )
                    ListItem(
                        headlineContent = { Text("Modo Oscuro") },
                        leadingContent = {
                            RadioButton(
                                selected = modoTemaActual == ModoTema.OSCURO,
                                onClick = null
                            )
                        },
                        modifier = Modifier.clickable { onCambiarTema(ModoTema.OSCURO) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.espacioChico))

                    ListItem(
                        headlineContent = { Text("Cerrar Sesión") },
                        leadingContent = {
                            Icon(painter = painterResource(R.drawable.ic_salir), contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        },
                        modifier = Modifier.clickable {
                            scope.launch { drawerState.close() }
                            onCerrarSesion()
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.menu_titulo_pantalla)) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_menu),
                                contentDescription = "Abrir menú lateral"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavegarPerfil) {
                            Icon(
                                painter = painterResource(R.drawable.ic_person),
                                contentDescription = "Ir a mi Perfil"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(Dimens.espacioGrande),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
            ) {
                Text(
                    text = "Bienvenido, ${uiState.nombreUsuario}",
                    style = MaterialTheme.typography.titleMedium
                )

                if (puedeGestionarEmpresas) {
                    TarjetaSeccion(titulo = "Módulo Clientes") {
                        Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                            Text("Gestión de Clientes e Instalaciones", style = MaterialTheme.typography.bodyLarge)
                            Button(
                                onClick = onNavegarRegistroCliente,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Registrar Nueva Empresa")
                            }
                        }
                    }
                }

                TarjetaSeccion(titulo = "Módulo Pruebas en Terreno (Induztek)") {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                        Text(
                            text = if (esClienteExterno) {
                                "Consulta de Protocolos e Historial"
                            } else {
                                "Protocolos y Certificaciones"
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )

                        if (!esClienteExterno) {
                            Button(
                                onClick = onNavegarRegistroPrueba,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Registrar Prueba (Cámara & GPS)")
                            }
                        }

                        OutlinedButton(
                            onClick = onNavegarHistorialEquipos,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Consultar Historial de Equipos")
                        }
                    }
                }
            }
        }
    }
}