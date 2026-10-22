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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.app.R
import com.induztek.app.model.MenuPrincipalUiState
import com.induztek.app.model.Usuario
import com.induztek.app.ui.components.TarjetaSeccion
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.viewmodel.MenuPrincipalViewModel
import kotlinx.coroutines.launch

//3. Constructor
@Composable
fun MenuPrincipalScreen(
    usuario: Usuario?,

    viewModel: MenuPrincipalViewModel = viewModel(),
    onNavegarRegistroCliente: () -> Unit,
    onNavegarListaClientes: () -> Unit,

    onVerPerfil: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    // Cada vez que 'usuario' cambie o la pantalla cargue, actualizamos el ViewModel
    LaunchedEffect(usuario) {
        viewModel.cargarUsuario(usuario)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MenuPrincipalContent(
        uiState = uiState,
        onNavegarRegistroCliente = onNavegarRegistroCliente,
        onNavegarListaClientes = onNavegarListaClientes,
        onCerrarSesion = onCerrarSesion
    )
}

//1. Contenido
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPrincipalContent(
    uiState: MenuPrincipalUiState,
    onNavegarRegistroCliente: () -> Unit,
    onNavegarListaClientes: () -> Unit,
    onCerrarSesion: () -> Unit = {}

) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                    modifier = Modifier.padding(bottom = Dimens.espacioMedio)
                )
                ListItem(
                    headlineContent = { Text("Listado de Clientes e Instalaciones") },
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_giro),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        scope.launch { drawerState.close() }
                        onNavegarListaClientes() // Acción que lleva a la nueva pantalla
                    }
                )
                ListItem(
                    headlineContent = { Text("Cerrar Sesión") },
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_salir),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
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
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_menu),
                                contentDescription = "Abrir menú lateral"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
    ) {
        innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(Dimens.espacioGrande),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
            ) {
                // Bienvenida
                Text(
                    text = "Bienvenido, ${uiState.nombreUsuario}",
                    style = MaterialTheme.typography.titleMedium
                )

                // Tarjeta de Módulo: Clientes e Instalaciones
                TarjetaSeccion(titulo = stringResource(R.string.menu_seccion_modulos)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                    ) {
                        Text(
                            text = stringResource(R.string.menu_card_clientes_titulo),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stringResource(R.string.menu_card_clientes_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onNavegarRegistroCliente,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.menu_btn_ingresar))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MenuPrincipalPreview() {
    MenuPrincipalContent(
        uiState = MenuPrincipalUiState(nombreUsuario = "Carlos Ingeniero"),
        onNavegarRegistroCliente = {},
        onNavegarListaClientes = {}
    )
}