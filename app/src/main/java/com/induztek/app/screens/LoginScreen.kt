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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginExitoso: (Usuario) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginContent(
        uiState = uiState,
        onCorreoCambia = viewModel::onCorreoCambia,
        onContrasenaCambia = viewModel::onContrasenaCambia,
        onAlternarVisible = viewModel::onAlternarVisibilidadContrasena,
        onIngresar = { viewModel.onIniciarSesion(onLoginExitoso) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    uiState: LoginUiState,
    onCorreoCambia: (String) -> Unit,
    onContrasenaCambia: (String) -> Unit,
    onAlternarVisible: () -> Unit,
    onIngresar: () -> Unit
) {
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
                }

                Spacer(modifier = Modifier.height(Dimens.espacioMedio))

                BotonPrincipal(
                    texto = "Iniciar Sesión",
                    onClick = onIngresar,
                    modifier = Modifier.fillMaxWidth()
                )

                CredencialesPrueba()

                Spacer(modifier = Modifier.height(Dimens.espacioGrande))
            }
        }
    }
}

@Composable
private fun CredencialesPrueba() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.espacioMedio),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.espacioMedio),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
        ) {
            Text(
                text = "💡 Credenciales de Prueba (Testing)",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            Text(
                text = "1. Técnico: mgodoy@induztek.ejemplo | Pass: 123456789",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "2. Ingeniero: acontreras@induztek.ejemplo | Pass: 987654321",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "3. Jefe Técnico: respinoza@induztek.ejemplo | Pass: 135792468",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "4. Cliente: mfuentes@envpacifico.ejemplo | Pass: 246813579",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    InduztekAppTheme {
        LoginContent(
            uiState = LoginUiState(),
            onCorreoCambia = {},
            onContrasenaCambia = {},
            onAlternarVisible = {},
            onIngresar = {}
        )
    }
}