package com.induztek.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.app.R
import com.induztek.app.model.RegistroUsuarioEmpresaUiState
import com.induztek.app.ui.components.CampoContrasena
import com.induztek.app.ui.components.CampoTexto
import com.induztek.app.ui.components.TarjetaSeccion
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.viewmodel.RegistroUsuarioEmpresaViewModel

@Composable
fun RegistroUsuarioEmpresaScreen(
    empresaId: Long = 0L,
    viewModel: RegistroUsuarioEmpresaViewModel = viewModel(),
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(empresaId) {
        viewModel.inicializarEmpresaId(empresaId)
    }

    LaunchedEffect(uiState.registroExitoso) {
        if (uiState.registroExitoso) {
            onVolver()
        }
    }

    RegistroUsuarioEmpresaContent(
        uiState = uiState,
        onRolCambia = viewModel::onRolCambia,
        onNombreCambia = viewModel::onNombreCambia,
        onCorreoCambia = viewModel::onCorreoCambia,
        onContrasenaCambia = viewModel::onContrasenaCambia,
        onAlternarVisible = viewModel::onAlternarVisibilidadContrasena,
        onRegistrar = viewModel::onRegistrar,
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegistroUsuarioEmpresaContent(
    uiState: RegistroUsuarioEmpresaUiState,
    onRolCambia: (Int) -> Unit,
    onNombreCambia: (String) -> Unit,
    onCorreoCambia: (String) -> Unit,
    onContrasenaCambia: (String) -> Unit,
    onAlternarVisible: () -> Unit,
    onRegistrar: () -> Unit,
    onVolver: () -> Unit
) {
    val esRepresentante = uiState.empresaId > 0L

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (esRepresentante) "Registro de Representante"
                        else "Registrar Nuevo Perfil Induztek"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            painter = painterResource(R.drawable.ic_volver),
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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
            if (!esRepresentante) {
                TarjetaSeccion(titulo = "Perfil / Rol del Usuario") {
                    val rolesInternos = listOf(
                        1 to "Técnico de Terreno",
                        2 to "Ingeniero de Pruebas",
                        3 to "Jefe Técnico"
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico),
                        verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                    ) {
                        rolesInternos.forEach { (idRol, nombreRol) ->
                            FilterChip(
                                selected = uiState.rolIdSeleccionado == idRol,
                                onClick = { onRolCambia(idRol) },
                                label = { Text(nombreRol) }
                            )
                        }
                    }
                }
            }

            TarjetaSeccion(
                titulo = if (esRepresentante) "Datos del Representante de la Empresa"
                else "Datos del Usuario"
            ) {
                CampoTexto(
                    valor = uiState.nombre,
                    onValorCambia = onNombreCambia,
                    etiqueta = "Nombre Completo",
                    error = uiState.errorNombre,
                    tipoTeclado = KeyboardType.Text,
                    icono = R.drawable.ic_person
                )

                CampoTexto(
                    valor = uiState.correo,
                    onValorCambia = onCorreoCambia,
                    etiqueta = "Correo Electrónico (ej: usuario@induztek.cl)",
                    error = uiState.errorCorreo,
                    tipoTeclado = KeyboardType.Email,
                    icono = R.drawable.ic_correo
                )

                CampoContrasena(
                    valor = uiState.contrasena,
                    onValorCambia = onContrasenaCambia,
                    etiqueta = "Contraseña (mínimo 8 caracteres)",
                    visible = uiState.contrasenaVisible,
                    onAlternarVisible = onAlternarVisible,
                    error = uiState.errorContrasena
                )

                Spacer(Modifier.height(Dimens.espacioChico))

                Button(
                    onClick = onRegistrar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = if (esRepresentante) "Registrar Representante" else "Registrar Usuario",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}