package com.induztek.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.app.R
import com.induztek.app.ui.components.TarjetaSeccion
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.viewmodel.RecuperarContrasenaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecuperarContrasenaScreen(
    viewModel: RecuperarContrasenaViewModel = viewModel(),
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar Contraseña") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(painter = painterResource(R.drawable.ic_volver), contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioGrande),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            TarjetaSeccion(titulo = "Restablecer Clave por Correo") {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                    Text(
                        text = "Ingresa tu correo registrado y te enviaremos un código de verificación.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedTextField(
                        value = uiState.correo,
                        onValueChange = viewModel::onCorreoCambia,
                        label = { Text("Correo Electrónico") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !uiState.cargando
                    )

                    if (!uiState.codigoEnviado) {
                        Button(
                            onClick = viewModel::enviarCodigo,
                            enabled = !uiState.cargando,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text(if (uiState.cargando) "ENVIANDO CÓDIGO..." else "ENVIAR CÓDIGO AL CORREO")
                        }
                    } else {
                        OutlinedTextField(
                            value = uiState.codigo,
                            onValueChange = viewModel::onCodigoCambia,
                            label = { Text("Código de 6 dígitos") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !uiState.cargando
                        )

                        OutlinedTextField(
                            value = uiState.nuevaContrasena,
                            onValueChange = viewModel::onNuevaContrasenaCambia,
                            label = { Text("Nueva Contraseña (mín. 8 caracteres)") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !uiState.cargando
                        )

                        Button(
                            onClick = viewModel::restablecerContrasena,
                            enabled = !uiState.cargando,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text(if (uiState.cargando) "ACTUALIZANDO..." else "ACTUALIZAR CONTRASEÑA")
                        }

                        TextButton(
                            onClick = viewModel::enviarCodigo,
                            enabled = !uiState.cargando,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Reenviar código")
                        }
                    }

                    uiState.mensajeError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    uiState.mensajeExito?.let {
                        Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}