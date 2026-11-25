package com.induztek.app.screens

import android.location.Geocoder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.induztek.app.R
import com.induztek.app.model.ProtocoloPrueba
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.viewmodel.RevisionPruebasViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionPruebasScreen(
    idUsuarioIngeniero: Long = 1L,
    esIngeniero: Boolean = true,
    viewModel: RevisionPruebasViewModel = viewModel(),
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val estadosFiltro = listOf(
        "PENDIENTE_APROBACION" to "Pendientes",
        "APROBADA" to "Aprobadas",
        "RECHAZADA" to "Rechazadas",
        "TODAS" to "Todas"
    )

    val pruebasFiltradas = if (uiState.filtroEstado == "TODAS") {
        uiState.pruebas
    } else {
        uiState.pruebas.filter { it.estadoProtocolo == uiState.filtroEstado }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Revisión de Pruebas") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            painter = painterResource(R.drawable.ic_volver),
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
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
            ) {
                items(estadosFiltro) { (valorEstado, etiqueta) ->
                    FilterChip(
                        selected = uiState.filtroEstado == valorEstado,
                        onClick = { viewModel.onCambiarFiltro(valorEstado) },
                        label = { Text(etiqueta) }
                    )
                }
            }

            uiState.mensajeEstado?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (pruebasFiltradas.isEmpty()) {
                Text(
                    text = "No hay pruebas en este estado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(pruebasFiltradas) { prueba ->
                        TarjetaPruebaRevision(
                            prueba = prueba,
                            esIngeniero = esIngeniero,
                            onRevisar = { viewModel.onAbrirDetalle(prueba) },
                            onAprobarDirecto = { viewModel.aprobarPrueba(prueba, idUsuarioIngeniero) }
                        )
                    }
                }
            }
        }
    }

    uiState.pruebaSeleccionada?.let { prueba ->
        AlertDialog(
            onDismissRequest = { viewModel.onCerrarDetalle() },
            title = { Text("Detalle de Prueba #${prueba.idProtocolo}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                    val etiquetaEquipo = prueba.codigoEquipo.ifBlank { "#${prueba.idEquipo}" }
                    val etiquetaTecnico = prueba.nombreTecnico.ifBlank { "#${prueba.idUsuarioTecnico}" }

                    Text("Tipo: ${prueba.tipoPrueba}", style = MaterialTheme.typography.bodyMedium)
                    Text("Medición: ${prueba.valorMedicion}", style = MaterialTheme.typography.bodyMedium)
                    Text("Equipo: $etiquetaEquipo | Estado: ${prueba.estadoProtocolo}", style = MaterialTheme.typography.bodySmall)
                    Text("Técnico: $etiquetaTecnico | Fecha: ${prueba.fechaHora}", style = MaterialTheme.typography.bodySmall)

                    if (prueba.observaciones.isNotBlank()) {
                        Text("Observaciones: ${prueba.observaciones}", style = MaterialTheme.typography.bodySmall)
                    }

                    TextoUbicacionRevision(
                        latitud = prueba.latitud,
                        longitud = prueba.longitud
                    )

                    prueba.fotoUri?.let { uriStr ->
                        AsyncImage(
                            model = uriStr,
                            contentDescription = "Evidencia de prueba",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (esIngeniero && prueba.estadoProtocolo == "PENDIENTE_APROBACION") {
                        OutlinedTextField(
                            value = uiState.motivoRechazo,
                            onValueChange = { viewModel.onMotivoRechazoCambia(it) },
                            label = { Text("Motivo (obligatorio si rechazas)") },
                            isError = uiState.errorMensaje != null,
                            supportingText = {
                                uiState.errorMensaje?.let { err ->
                                    Text(err, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (esIngeniero && prueba.estadoProtocolo == "PENDIENTE_APROBACION") {
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                        OutlinedButton(
                            onClick = { viewModel.rechazarPrueba(prueba, idUsuarioIngeniero) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Rechazar")
                        }
                        Button(
                            onClick = { viewModel.aprobarPrueba(prueba, idUsuarioIngeniero) }
                        ) {
                            Text("Aprobar")
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onCerrarDetalle() }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
private fun TarjetaPruebaRevision(
    prueba: ProtocoloPrueba,
    esIngeniero: Boolean,
    onRevisar: () -> Unit,
    onAprobarDirecto: () -> Unit
) {
    val etiquetaEquipo = prueba.codigoEquipo.ifBlank { "#${prueba.idEquipo}" }
    val etiquetaTecnico = prueba.nombreTecnico.ifBlank { "#${prueba.idUsuarioTecnico}" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.espacioMedio),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
        ) {
            Text(
                text = "${prueba.tipoPrueba} — ${prueba.valorMedicion}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Equipo: $etiquetaEquipo | Estado: ${prueba.estadoProtocolo}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Fecha: ${prueba.fechaHora} | Técnico: $etiquetaTecnico",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextoUbicacionRevision(
                latitud = prueba.latitud,
                longitud = prueba.longitud
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(onClick = onRevisar) {
                    Text("Ver / Revisar")
                }
                if (esIngeniero && prueba.estadoProtocolo == "PENDIENTE_APROBACION") {
                    Button(onClick = onAprobarDirecto) {
                        Text("Aprobar")
                    }
                }
            }
        }
    }
}

@Composable
private fun TextoUbicacionRevision(
    latitud: Double?,
    longitud: Double?
) {
    if (latitud == null || longitud == null) return

    val context = LocalContext.current
    var direccion by remember(latitud, longitud) { mutableStateOf<String?>(null) }

    LaunchedEffect(latitud, longitud) {
        direccion = withContext(Dispatchers.IO) {
            try {
                @Suppress("DEPRECATION")
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val lista = geocoder.getFromLocation(latitud, longitud, 1)
                lista?.firstOrNull()?.getAddressLine(0)
            } catch (_: Exception) {
                null
            }
        }
    }

    Text(
        text = buildString {
            append("GPS: $latitud, $longitud")
            if (!direccion.isNullOrBlank()) {
                append("\nDirección: $direccion")
            }
        },
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary
    )
}