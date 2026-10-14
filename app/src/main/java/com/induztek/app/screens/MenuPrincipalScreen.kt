package com.induztek.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.app.R
import com.induztek.app.model.MenuPrincipalUiState
import com.induztek.app.ui.components.TarjetaSeccion
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.viewmodel.MenuPrincipalViewModel

//3. Constructor
@Composable
fun MenuPrincipalScreen(
    viewModel: MenuPrincipalViewModel = viewModel(),
    onNavegarRegistroCliente: () -> Unit,

    onVerPerfil: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MenuPrincipalContent(
        uiState = uiState,
        onNavegarRegistroCliente = onNavegarRegistroCliente
    )
}

//1. Contenido
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPrincipalContent(
    uiState: MenuPrincipalUiState,
    onNavegarRegistroCliente: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_titulo_pantalla)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor =MaterialTheme.colorScheme.onPrimary
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MenuPrincipalPreview() {
    MenuPrincipalContent(
        uiState = MenuPrincipalUiState(nombreUsuario = "Carlos Ingeniero"),
        onNavegarRegistroCliente = {}
    )
}