package com.induztek.app.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.induztek.app.R
import com.induztek.app.ui.components.TarjetaSeccion
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.viewmodel.RegistroPruebaViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroPruebaScreen(
    idUsuarioTecnico: Long = 1L,
    viewModel: RegistroPruebaViewModel = viewModel(),
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var mostrarDialogoFoto by remember { mutableStateOf(false) }
    var menuEquiposDesplegado by remember { mutableStateOf(false) }

    // --- 1. SELECCIÓN DE FOTO (GALERÍA) ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onFotoCapturada(it.toString()) }
    }

    val permisoGaleria = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permisoGaleriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { esConcedido ->
        if (esConcedido) {
            galleryLauncher.launch("image/*")
        } else {
            galleryLauncher.launch("image/*")
        }
    }

    // --- 2. CAPTURA DE FOTO (CÁMARA) ---
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val uriTemp = guardarBitmapEnUriPrueba(context, it)
            uriTemp?.let { uri -> viewModel.onFotoCapturada(uri.toString()) }
        }
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { esConcedido ->
        if (esConcedido) {
            camaraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Se requiere permiso de cámara para tomar fotos", Toast.LENGTH_SHORT).show()
        }
    }

    // --- 3. GEOLOCALIZACIÓN GPS ---
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.obtenerUbicacionGps()
        } else {
            Toast.makeText(context, "Se requiere permiso de ubicación para registrar GPS", Toast.LENGTH_SHORT).show()
        }
    }

    // Funciones Auxiliares para verificar permisos
    fun abrirGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            galleryLauncher.launch("image/*")
        } else {
            val tienePermiso = ContextCompat.checkSelfPermission(context, permisoGaleria) == PackageManager.PERMISSION_GRANTED
            if (tienePermiso) {
                galleryLauncher.launch("image/*")
            } else {
                permisoGaleriaLauncher.launch(permisoGaleria)
            }
        }
    }

    fun abrirCamara() {
        val tienePermiso = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (tienePermiso) {
            camaraLauncher.launch(null)
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun solicitarUbicacion() {
        val tienePermiso = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (tienePermiso) {
            viewModel.obtenerUbicacionGps()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Notificación de éxito
    LaunchedEffect(uiState.registroExitoso) {
        if (uiState.registroExitoso) {
            viewModel.limpiarFormulario()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Prueba en Terreno") },
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
                .verticalScroll(rememberScrollState())
                .padding(Dimens.espacioGrande),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            // Tarjeta 1: Selección del Equipo
            TarjetaSeccion(titulo = "1. Datos del Equipo") {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                    Text("Seleccione Equipo a Probar:", style = MaterialTheme.typography.bodyMedium)

                    ExposedDropdownMenuBox(
                        expanded = menuEquiposDesplegado,
                        onExpandedChange = { menuEquiposDesplegado = !menuEquiposDesplegado }
                    ) {
                        val equipoActual = uiState.listaEquipos.find { it.idEquipo == uiState.idEquipoSeleccionado }
                        OutlinedTextField(
                            value = equipoActual?.let { "${it.codigoEquipo} (${it.tipoEquipo})" } ?: "Sin equipos disponibles",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuEquiposDesplegado) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = menuEquiposDesplegado,
                            onDismissRequest = { menuEquiposDesplegado = false }
                        ) {
                            uiState.listaEquipos.forEach { eq ->
                                DropdownMenuItem(
                                    text = { Text("${eq.codigoEquipo} (${eq.tipoEquipo})") },
                                    onClick = {
                                        viewModel.onEquipoSeleccionado(eq.idEquipo)
                                        menuEquiposDesplegado = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Tarjeta 2: Mediciones Técnicas
            TarjetaSeccion(titulo = "2. Mediciones y Resultados") {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                    OutlinedTextField(
                        value = uiState.tipoPrueba,
                        onValueChange = { viewModel.onTipoPruebaCambia(it) },
                        label = { Text("Tipo de Prueba") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.valorMedicion,
                        onValueChange = { viewModel.onValorMedicionCambia(it) },
                        label = { Text("Valor de Medición (ej: 500 MΩ, 220V)") },
                        isError = uiState.errorValorMedicion != null,
                        supportingText = {
                            uiState.errorValorMedicion?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Resultado de la prueba:", style = MaterialTheme.typography.bodyMedium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
                    ) {
                        FilterChip(
                            selected = uiState.resultado == "APROBADO",
                            onClick = { viewModel.onResultadoCambia("APROBADO") },
                            label = { Text("APROBADO") }
                        )
                        FilterChip(
                            selected = uiState.resultado == "RECHAZADO",
                            onClick = { viewModel.onResultadoCambia("RECHAZADO") },
                            label = { Text("RECHAZADO") }
                        )
                    }

                    OutlinedTextField(
                        value = uiState.observaciones,
                        onValueChange = { viewModel.onObservacionesCambia(it) },
                        label = { Text("Observaciones / Notas de terreno") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Tarjeta 3: Evidencias y APIs Nativas (Cámara + GPS)
            TarjetaSeccion(titulo = "3. Evidencia Nativa (Cámara & GPS)") {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {

                    // Selector de Evidencia (Cámara / Galería)
                    Button(
                        onClick = { mostrarDialogoFoto = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (uiState.fotoUri == null) "Adjuntar Evidencia Fotográfica" else "Cambiar Evidencia")
                    }

                    uiState.fotoUri?.let { uriStr ->
                        Text("Evidencia seleccionada:", style = MaterialTheme.typography.bodySmall)
                        AsyncImage(
                            model = uriStr,
                            contentDescription = "Evidencia Fotográfica",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.espacioChico))

                    // API Nativa 2: Geolocalización GPS
                    OutlinedButton(
                        onClick = { solicitarUbicacion() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Capturar Coordenadas GPS")
                    }

                    if (uiState.latitud != null && uiState.longitud != null) {
                        Text(
                            text = "Ubicación GPS registrada:\nLat: ${uiState.latitud} | Lon: ${uiState.longitud}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Botón Principal de Guardar Protocolo
            Button(
                onClick = { viewModel.onGuardarProtocolo(idUsuarioTecnico) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("REGISTRAR PROTOCOLO EN TERRENO")
            }

            if (uiState.registroExitoso) {
                Text(
                    text = "✓ Protocolo registrado con éxito en estado REGISTRADO_TERRENO",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    // Diálogo emergente para elegir la fuente de evidencia (Cámara o Galería)
    if (mostrarDialogoFoto) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoFoto = false },
            title = { Text("Evidencia Fotográfica") },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text("Tomar foto con la cámara") },
                        leadingContent = { Icon(painter = painterResource(R.drawable.ic_person), contentDescription = null) },
                        modifier = Modifier.clickable {
                            mostrarDialogoFoto = false
                            abrirCamara()
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Elegir de la galería") },
                        leadingContent = { Icon(painter = painterResource(R.drawable.ic_person), contentDescription = null) },
                        modifier = Modifier.clickable {
                            mostrarDialogoFoto = false
                            abrirGaleria()
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { mostrarDialogoFoto = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Convertidor de Bitmap a Uri para previsualización
private fun guardarBitmapEnUriPrueba(context: Context, bitmap: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "evidencia_${System.currentTimeMillis()}", null)
    return path?.let { Uri.parse(it) }
}