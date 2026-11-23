package com.induztek.app.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.app.ui.components.CampoContrasena
import com.induztek.app.ui.components.TarjetaSeccion
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.viewmodel.CrearContrasenaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearContrasenaScreen(
    idUsuario: Long,
    alNavegarAlInicio: () -> Unit,
    viewModel: CrearContrasenaViewModel = viewModel()
) {
    val estadoUi by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(enabled = true) { }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Acceso") },
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.espacioGrande),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            TarjetaSeccion(titulo = "Crea tu nueva contraseña") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                ) {
                    Text(
                        text = "Para mayor seguridad, te pedimos que reemplaces la contraseña temporal que enviamos a tu correo por una personalizada.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify
                    )

                    CampoContrasena(
                        valor = estadoUi.nuevaContrasena,
                        onValorCambia = viewModel::onContrasenaCambia,
                        etiqueta = "Nueva Contraseña (mín. 8 caracteres)",
                        visible = estadoUi.contrasenaVisible,
                        onAlternarVisible = viewModel::onAlternarVisibilidadContrasena,
                        error = estadoUi.errorContrasena
                    )

                    CampoContrasena(
                        valor = estadoUi.confirmarContrasena,
                        onValorCambia = viewModel::onConfirmarContrasenaCambia,
                        etiqueta = "Confirmar Contraseña",
                        visible = estadoUi.contrasenaVisible,
                        onAlternarVisible = viewModel::onAlternarVisibilidadContrasena,
                        error = estadoUi.errorConfirmar
                    )

                    Button(
                        onClick = { viewModel.onGuardarContrasena(idUsuario, alNavegarAlInicio) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !estadoUi.cargando
                    ) {
                        Text(if (estadoUi.cargando) "GUARDANDO..." else "GUARDAR CONTRASEÑA")
                    }

                    estadoUi.errorGeneral?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}