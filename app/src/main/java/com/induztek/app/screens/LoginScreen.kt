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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.app.R
import com.induztek.app.model.LoginUiState
import com.induztek.app.model.Usuario
import com.induztek.app.ui.components.BotonPrincipal
import com.induztek.app.ui.components.CampoContrasena
import com.induztek.app.ui.components.CampoTexto
import com.induztek.app.ui.components.ImagenEncabezado
import com.induztek.app.ui.components.TarjetaSeccion
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.ui.theme.InduztekAppTheme
import com.induztek.app.viewmodel.LoginViewModel

//imp emer

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginExitoso: (Usuario) -> Unit,
    onNavegarRecuperarContrasena: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginContent(
        uiState = uiState,
        onCorreoCambia = viewModel::onCorreoCambia,
        onContrasenaCambia = viewModel::onContrasenaCambia,
        onAlternarVisible = viewModel::onAlternarVisibilidadContrasena,
        onIngresar = { viewModel.onIniciarSesion(onLoginExitoso) },
        onNavegarRecuperarContrasena = onNavegarRecuperarContrasena
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    uiState: LoginUiState,
    onCorreoCambia: (String) -> Unit,
    onContrasenaCambia: (String) -> Unit,
    onAlternarVisible: () -> Unit,
    onIngresar: () -> Unit,
    onNavegarRecuperarContrasena: () -> Unit = {}
) {
    var mostrarEmergente by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Induztek - Iniciar Sesión") },
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
        ) {
            ImagenEncabezado(
                imagen = R.drawable.banner_login,
                descripcion = "Banner Induztek",
                titulo = "Control de Pruebas",
                subtitulo = "Ingrese sus credenciales de acceso"
            )

            Column(
                modifier = Modifier.padding(Dimens.espacioGrande),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
            ) {
                TarjetaSeccion(titulo = "Credenciales de Usuario") {
                    CampoTexto(
                        valor = uiState.correo,
                        onValorCambia = onCorreoCambia,
                        etiqueta = "Correo Electrónico (ej: tecnico@induztek.cl)",
                        error = uiState.errorCorreo,
                        tipoTeclado = KeyboardType.Email,
                        icono = R.drawable.ic_correo
                    )

                    Spacer(modifier = Modifier.height(Dimens.espacioChico))

                    CampoContrasena(
                        valor = uiState.contrasena,
                        onValorCambia = onContrasenaCambia,
                        etiqueta = "Contraseña",
                        visible = uiState.contrasenaVisible,
                        onAlternarVisible = onAlternarVisible,
                        error = uiState.errorContrasena
                    )

                    TextButton(
                        onClick = onNavegarRecuperarContrasena,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.espacioMedio))

                BotonPrincipal(
                    texto = "Iniciar Sesión",
                    onClick = onIngresar,
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(
                    onClick = { mostrarEmergente = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Ver información del sistema")
                }

                Spacer(modifier = Modifier.height(Dimens.espacioGrande))
            }
        }
    }
    if (mostrarEmergente) {
        AlertDialog(

            onDismissRequest = { mostrarEmergente = false },
            title = {
                Text("Información de la App")
            },
            text = {
                Text("Aplicación Induztek v1.0\nDesarrollada para el control y gestión de protocolos de pruebas técnicas en terreno.")
            },
            confirmButton = {
                TextButton(
                    onClick = { mostrarEmergente = false }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarEmergente = false }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}

//--------- CREDENCIALES -------------
//1. Técnico: mgodoy@induztek.ejemplo | Pass: 123456789
//2. Ingeniero: acontreras@induztek.ejemplo | Pass: 987654321
//3. Jefe Técnico: respinoza@induztek.ejemplo | Pass: 135792468
//4. Cliente: mfuentes@envpacifico.ejemplo | Pass: 246813579

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    InduztekAppTheme {
        LoginContent(
            uiState = LoginUiState(),
            onCorreoCambia = {},
            onContrasenaCambia = {},
            onAlternarVisible = {},
            onIngresar = {},
            onNavegarRecuperarContrasena = {}
        )
    }
}