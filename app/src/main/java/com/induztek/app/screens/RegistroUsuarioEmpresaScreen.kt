package com.induztek.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.app.R
import com.induztek.app.model.RegistroUsuarioEmpresaUiState
import com.induztek.app.ui.components.CampoContrasena
import com.induztek.app.ui.components.CampoTexto
import com.induztek.app.ui.components.SeleccionPreguntaSeguridad
import com.induztek.app.ui.components.TarjetaSeccion
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.viewmodel.RegistroUsuarioEmpresaViewModel

//3. Constructor
@Composable
fun RegistroUsuarioEmpresaScreen(
    empresaId: Long, //Recibimos empresaId como parámetro
    viewModel: RegistroUsuarioEmpresaViewModel = viewModel(),
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    //Carga la empresaId en el ViewModel cuando la pantalla se inicia
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
        onNombreCambia = viewModel::onNombreCambia,
        onCorreoCambia = viewModel::onCorreoCambia,
        onContrasenaCambia = viewModel::onContrasenaCambia,
        onTipoPreguntaCambia = viewModel::onTipoPreguntaClaveCambia,
        onRespuestaPreguntaCambia = viewModel::onRespuestaPreguntaClaveCambia,
        onAlternarVisible = viewModel::onAlternarVisibilidadContrasena,
        onRegistrar = viewModel::onRegistrar,
        onVolver = onVolver
    )
}

//1. Contenido
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroUsuarioEmpresaContent(
    uiState: RegistroUsuarioEmpresaUiState,
    onNombreCambia: (String) -> Unit,
    onCorreoCambia: (String) -> Unit,
    onContrasenaCambia: (String) -> Unit,
    onTipoPreguntaCambia: (Int) -> Unit,
    onRespuestaPreguntaCambia: (String) -> Unit,
    onAlternarVisible: () -> Unit,
    onRegistrar: () -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Representante de la Empresa") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onVolver) {
                        Icon(
                            painter = painterResource(R.drawable.ic_volver), // Asegúrate de tener tu ícono de volver
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor =MaterialTheme.colorScheme.onPrimary
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
            // Tarjeta 1. Datos del representante de la empresa
            TarjetaSeccion(titulo = "Datos del Representante de la Empresa") {
                CampoTexto(
                    valor = uiState.nombre,
                    onValorCambia = onNombreCambia,
                    etiqueta = "Nombre de Representante de la Empresa",
                    error = uiState.errorNombre,
                    tipoTeclado = KeyboardType.Text,
                    icono = R.drawable.ic_person
                )

                CampoTexto(
                    valor = uiState.correo,
                    onValorCambia = onCorreoCambia,
                    etiqueta = "Correo Electrónico (ej: jose@empresa.com)",
                    error = uiState.errorCorreo,
                    tipoTeclado = KeyboardType.Email,
                    icono = R.drawable.ic_correo
                )

                CampoContrasena(
                    valor = uiState.contrasena,
                    onValorCambia = onContrasenaCambia,
                    etiqueta = "Contraseña",
                    visible = uiState.contrasenaVisible,
                    onAlternarVisible = onAlternarVisible,
                    error = uiState.errorContrasena
                )

                SeleccionPreguntaSeguridad(
                    preguntaSeleccionadaId = uiState.tipoPreguntaClave,
                    onPreguntaSeleccionada = { nuevoId ->
                        onTipoPreguntaCambia(nuevoId)
                    }
                )

                CampoTexto(
                    valor = uiState.respuestaPreguntaClave,
                    onValorCambia = onRespuestaPreguntaCambia,
                    etiqueta = "Respuesta de Seguridad",
                    error = uiState.errorRespuestaPregunta,
                    tipoTeclado = KeyboardType.Text,
                    icono = R.drawable.ic_contrasena
                )
            }

            Spacer(Modifier.height(Dimens.espacioGrande))

            Button(
                onClick = onRegistrar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.espacioMedio)
            ) {
                Text(
                    text = "Registrar Representante de la Empresa",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}