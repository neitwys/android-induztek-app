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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.induztek.app.R
import com.induztek.app.model.Usuario
import com.induztek.app.ui.components.TarjetaSeccion
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.viewmodel.PerfilViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuario: Usuario?,
    viewModel: PerfilViewModel = viewModel(),
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(usuario) {
        viewModel.cargarDatosUsuario(usuario)
    }

    var mostrarDialogoOpciones by remember { mutableStateOf(false) }

    // 1. Selector de Galería
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.actualizarFotoPerfil(it) }
    }

    // 2. Launcher de Cámara seguro (retorna Bitmap directamente)
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val uriTemp = guardarBitmapEnUri(context, it)
            uriTemp?.let { uri -> viewModel.actualizarFotoPerfil(uri) }
        }
    }

    // Permisos
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { esConcedido ->
        if (esConcedido) {
            camaraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Se requiere permiso de cámara para tomar fotos", Toast.LENGTH_SHORT).show()
        }
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
            galeriaLauncher.launch("image/*")
        } else {
            // En versiones nuevas de Android el picker nativo funciona directo
            galeriaLauncher.launch("image/*")
        }
    }

    fun abrirGaleria() {
        val tienePermiso = ContextCompat.checkSelfPermission(context, permisoGaleria) == PackageManager.PERMISSION_GRANTED
        if (tienePermiso) {
            galeriaLauncher.launch("image/*")
        } else {
            // Solicita explícitamente el permiso antes de abrir
            permisoGaleriaLauncher.launch(permisoGaleria)
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

    val usuarioActual = uiState.usuario ?: usuario

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de Usuario") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            // Sección Foto de Perfil
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.padding(vertical = Dimens.espacioChico)
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { mostrarDialogoOpciones = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.fotoUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(uiState.fotoUri),
                            contentDescription = "Foto de Perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_person),
                            contentDescription = "Foto Por Defecto",
                            modifier = Modifier.size(70.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Botón encima de la foto
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { mostrarDialogoOpciones = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_person),
                        contentDescription = "Cambiar Foto",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = usuarioActual?.nombre ?: "Usuario Induztek",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = usuarioActual?.rolNombre ?: "Rol no asignado",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            uiState.mensajeExito?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(Dimens.espacioChico))

            // Información Detallada
            TarjetaSeccion(titulo = "Información Personal y Acceso") {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)) {
                    ItemInfoPerfil(
                        etiqueta = "Correo Corporativo",
                        valor = usuarioActual?.correo ?: "Sin correo",
                        iconoRes = R.drawable.ic_correo
                    )

                    ItemInfoPerfil(
                        etiqueta = "Rol del Sistema",
                        valor = usuarioActual?.rolNombre ?: "Técnico",
                        iconoRes = R.drawable.ic_person
                    )

                    ItemInfoPerfil(
                        etiqueta = "Empresa / Representante de la Empresa",
                        valor = usuarioActual?.empresaNombre ?: "Induztek Servicios Industriales",
                        iconoRes = R.drawable.ic_instalacion
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.espacioGrande))

            // Botón de Cerrar Sesión
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCerrarSesion() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.espacioMedio),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_salir),
                        contentDescription = "Cerrar Sesión",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(Dimens.espacioChico))
                    Text(
                        text = "CERRAR SESIÓN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }

    // Diálogo emergente Opciones de Imagen
    if (mostrarDialogoOpciones) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoOpciones = false },
            title = { Text("Seleccionar foto de perfil") },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text("Tomar foto con la cámara") },
                        leadingContent = { Icon(painter = painterResource(R.drawable.ic_person), contentDescription = null) },
                        modifier = Modifier.clickable {
                            mostrarDialogoOpciones = false
                            abrirCamara()
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Elegir de la galería") },
                        leadingContent = { Icon(painter = painterResource(R.drawable.ic_person), contentDescription = null) },
                        modifier = Modifier.clickable {
                            mostrarDialogoOpciones = false
                            abrirGaleria()
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { mostrarDialogoOpciones = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Función auxiliar para convertir el Bitmap capturado por la cámara en Uri para Coil
private fun guardarBitmapEnUri(context: Context, bitmap: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "foto_perfil_${System.currentTimeMillis()}", null)
    return path?.let { Uri.parse(it) }
}

@Composable
private fun ItemInfoPerfil(
    etiqueta: String,
    valor: String,
    iconoRes: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconoRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(Dimens.espacioMedio))
        Column {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}