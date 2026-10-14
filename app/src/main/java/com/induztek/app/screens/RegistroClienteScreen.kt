package com.induztek.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.app.R
import com.induztek.app.model.RegistroClienteUiState
import com.induztek.app.ui.components.BotonPrincipal
import com.induztek.app.ui.components.BotonSecundario
import com.induztek.app.ui.components.CampoTexto
import com.induztek.app.ui.components.TarjetaSeccion
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.ui.theme.Exito
import com.induztek.app.ui.theme.ExitoFondo
import com.induztek.app.viewmodel.RegistroClienteViewModel


// 3. Constructor
@Composable
fun RegistroClienteScreen(
    viewModel: RegistroClienteViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RegistroClienteContent(
        uiState = uiState,
        onRazonSocialCambia = viewModel::onRazonSocialCambia,
        onRutCambia = viewModel::onRutCambia,
        onGiroCambia = viewModel::onGiroCambia,
        onContactoCambia = viewModel::onContactoCambia,
        onTelefonoCambia = viewModel::onTelefonoCambia,
        onCorreoCambia = viewModel::onCorreoCambia,
        onNombreInstalacionCambia = viewModel::onNombreInstalacionCambia,
        onUbicacionCambia = viewModel::onUbicacionCambia,
        onComunaCambia = viewModel::onComunaCambia,
        onRegionCambia = viewModel::onRegionCambia,
        onObservacionesAccesoCambia = viewModel::onObservacionesAccesoCambia,
        onLimpiar = viewModel::onLimpiar,
        onRegistrar = viewModel::onRegistrar
    )
}

// 1. Contenido
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroClienteContent(
    uiState: RegistroClienteUiState,
    onRazonSocialCambia: (String) -> Unit,
    onRutCambia: (String) -> Unit,
    onGiroCambia: (String) -> Unit,
    onContactoCambia: (String) -> Unit,
    onTelefonoCambia: (String) -> Unit,
    onCorreoCambia: (String) -> Unit,
    onNombreInstalacionCambia: (String) -> Unit,
    onUbicacionCambia: (String) -> Unit,
    onComunaCambia: (String) -> Unit,
    onRegionCambia: (String) -> Unit,
    onObservacionesAccesoCambia: (String) -> Unit,
    onLimpiar: () -> Unit,
    onRegistrar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cliente_titulo_pantalla)) },
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
            // Tarjeta 1. Datos del cliente
            TarjetaSeccion(titulo = stringResource(R.string.cliente_seccion_datos)) {
                CampoTexto(
                    valor = uiState.razonSocial,
                    onValorCambia = onRazonSocialCambia,
                    etiqueta = stringResource(R.string.cliente_campo_razon_social),
                    error = uiState.errorRazonSocial,
                    tipoTeclado = KeyboardType.Text,
                    icono = R.drawable.ic_person
                )

                CampoTexto(
                    valor = uiState.rut,
                    onValorCambia = onRutCambia,
                    etiqueta = stringResource(R.string.cliente_campo_rut),
                    error = uiState.errorRut,
                    tipoTeclado = KeyboardType.Text,
                    icono = R.drawable.ic_rut
                )

                CampoTexto(
                    valor = uiState.giro,
                    onValorCambia = onGiroCambia,
                    etiqueta = stringResource(R.string.cliente_campo_giro),
                    error = uiState.errorGiro,
                    tipoTeclado = KeyboardType.Text,
                    icono = R.drawable.ic_giro
                )

                CampoTexto(
                    valor = uiState.contacto,
                    onValorCambia = onContactoCambia,
                    etiqueta = stringResource(R.string.cliente_campo_contacto),
                    error = uiState.errorContacto,
                    tipoTeclado = KeyboardType.Text,
                    icono = R.drawable.ic_contacto
                )

                CampoTexto(
                    valor = uiState.telefono,
                    onValorCambia = onTelefonoCambia,
                    etiqueta = stringResource(R.string.cliente_campo_telefono),
                    error = uiState.errorTelefono,
                    tipoTeclado = KeyboardType.Phone,
                    icono = R.drawable.ic_telefono
                )

                CampoTexto(
                    valor = uiState.correo,
                    onValorCambia = onCorreoCambia,
                    etiqueta = stringResource(R.string.cliente_campo_correo),
                    error = uiState.errorCorreo,
                    tipoTeclado = KeyboardType.Email,
                    icono = R.drawable.ic_correo
                )
            }

            // TARJETA 2 - DATOS DE LA INSTALACIÓN
            TarjetaSeccion(titulo = stringResource(R.string.cliente_seccion_instalacion)) {
                CampoTexto(
                    valor = uiState.nombreInstalacion,
                    onValorCambia = onNombreInstalacionCambia,
                    etiqueta = stringResource(R.string.cliente_campo_nombre_instalacion),
                    error = uiState.errorNombreInstalacion,
                    tipoTeclado = KeyboardType.Text,
                    icono = R.drawable.ic_instalacion
                )

                CampoTexto(
                    valor = uiState.ubicacion,
                    onValorCambia = onUbicacionCambia,
                    etiqueta = stringResource(R.string.cliente_campo_ubicacion),
                    error = uiState.errorUbicacion,
                    tipoTeclado = KeyboardType.Text,
                    icono = R.drawable.ic_ubicacion
                )

                CampoTexto(
                    valor = uiState.comuna,
                    onValorCambia = onComunaCambia,
                    etiqueta = stringResource(R.string.cliente_campo_comuna),
                    error = uiState.errorComuna,
                    tipoTeclado = KeyboardType.Text,
                    icono = R.drawable.ic_comuna
                )

                CampoTexto(
                    valor = uiState.region,
                    onValorCambia = onRegionCambia,
                    etiqueta = stringResource(R.string.cliente_campo_region),
                    error = uiState.errorRegion,
                    tipoTeclado = KeyboardType.Text,
                    icono = R.drawable.ic_region
                )

                CampoTexto(
                    valor = uiState.observacionesAcceso,
                    onValorCambia = onObservacionesAccesoCambia,
                    etiqueta = stringResource(R.string.cliente_campo_observaciones),
                    error = uiState.errorObservacionesAcceso,
                    tipoTeclado = KeyboardType.Text,
                    icono = R.drawable.ic_observaciones
                )
            }

            // BOTONES DE ACCIÓN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
            ) {
                BotonSecundario(
                    texto = stringResource(R.string.cliente_btn_limpiar),
                    onClick = onLimpiar,
                    modifier = Modifier.weight(1f)
                )
                BotonPrincipal(
                    texto = stringResource(R.string.cliente_btn_registrar),
                    onClick = onRegistrar,
                    modifier = Modifier.weight(1f)
                )
            }

            // Mensaje de exito
            if (uiState.registroExitoso) {
                MensajeExitoRegistro(uiState.razonSocial)
            }
            Spacer(Modifier.height(Dimens.espacioGrande))
        }
    }
}

// 1.5 Funcion de mensaje de exito
@Composable
private fun MensajeExitoRegistro(nombre: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ExitoFondo, contentColor = Exito)
    ) {
        Row(
            modifier = Modifier.padding(Dimens.espacioChico),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = Exito,
                modifier = Modifier.size(Dimens.tamanoIcono)
            )
            Text(
                text = stringResource(R.string.cliente_msg_exito),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = Dimens.espacioChico)
            )
        }
    }
}

// 2. Preview
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistroClienteContentPreview() {
    val estadoSimulado = RegistroClienteUiState(
        razonSocial = "Envasadora del Pacífico SpA",
        rut = "76.543.210-9",
        giro = "Industrial / Eléctrico",
        contacto = "Carlos Soto",
        telefono = "+56912345678",
        correo = "contacto@envasadora.cl",
        nombreInstalacion = "Planta Lampa",
        ubicacion = "Ruta 5 Norte Km 28",
        comuna = "Lampa",
        region = "Metropolitana",
        observacionesAcceso = "Requiere inducción de seguridad previa"
    )

    RegistroClienteContent(
        uiState = estadoSimulado,
        onRazonSocialCambia = {},
        onRutCambia = {},
        onGiroCambia = {},
        onContactoCambia = {},
        onTelefonoCambia = {},
        onCorreoCambia = {},
        onNombreInstalacionCambia = {},
        onUbicacionCambia = {},
        onComunaCambia = {},
        onRegionCambia = {},
        onObservacionesAccesoCambia = {},
        onLimpiar = {},
        onRegistrar = {}
    )
}