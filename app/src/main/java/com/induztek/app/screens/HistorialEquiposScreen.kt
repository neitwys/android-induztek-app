package com.induztek.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.induztek.app.R
import com.induztek.app.model.ProtocoloPrueba
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.ui.theme.InduztekAppTheme
import com.induztek.app.viewmodel.HistorialEquiposViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialEquiposScreen(
    viewModel: HistorialEquiposViewModel = viewModel(),
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Equipos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            painter = painterResource(R.drawable.ic_salir),
                            contentDescription = "Volver"
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
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioGrande),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            // Buscador por Código de Equipo
            OutlinedTextField(
                value = uiState.busquedaCodigo,
                onValueChange = { viewModel.onBusquedaCambia(it) },
                label = { Text("Buscar por código de equipo (ej: TR-001)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (uiState.listaProtocolos.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No se encontraron protocolos para el equipo ingresado.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.listaProtocolos) { protocolo ->
                        TarjetaProtocoloItem(protocolo = protocolo)
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaProtocoloItem(protocolo: ProtocoloPrueba) {
    val esAprobado = protocolo.resultado == "APROBADO"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.espacioMedio),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${protocolo.codigoEquipo} (${protocolo.tipoEquipo})",
                    style = MaterialTheme.typography.titleMedium
                )
                SuggestionChip(
                    onClick = {},
                    label = { Text(protocolo.resultado) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (esAprobado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                        labelColor = if (esAprobado) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                )
            }

            Text(
                text = "Prueba: ${protocolo.tipoPrueba}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Medición: ${protocolo.valorMedicion}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Fecha/Hora: ${protocolo.fechaHora}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Estado: ${protocolo.estadoProtocolo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )

            if (protocolo.observaciones.isNotBlank()) {
                Text(
                    text = "Obs: ${protocolo.observaciones}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Muestra ubicación GPS si existe
            if (protocolo.latitud != null && protocolo.longitud != null) {
                Text(
                    text = "Ubicación GPS: ${protocolo.latitud}, ${protocolo.longitud}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            // Muestra la imagen capturada si existe
            protocolo.fotoUri?.let { uriStr ->
                Spacer(modifier = Modifier.height(4.dp))
                AsyncImage(
                    model = uriStr,
                    contentDescription = "Foto de evidencia",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

