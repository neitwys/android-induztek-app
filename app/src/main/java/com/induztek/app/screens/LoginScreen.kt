package com.induztek.app.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.app.R
import com.induztek.app.model.LoginUiState
import com.induztek.app.model.Usuario
import com.induztek.app.ui.components.BotonPrincipal
import com.induztek.app.ui.components.CampoContrasena
import com.induztek.app.ui.components.CampoTexto
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.ui.theme.InduztekAppTheme
import com.induztek.app.viewmodel.LoginViewModel

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

    var iniciarAnimacion by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        iniciarAnimacion = true
    }

    val alphaCabecera by animateFloatAsState(
        targetValue = if (iniciarAnimacion) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "alphaCabecera"
    )
    val desplazamientoCabecera by animateFloatAsState(
        targetValue = if (iniciarAnimacion) 0f else -30f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "desplazamientoCabecera"
    )

    val alphaTarjeta by animateFloatAsState(
        targetValue = if (iniciarAnimacion) 1f else 0f,
        animationSpec = tween(durationMillis = 700, delayMillis = 150, easing = FastOutSlowInEasing),
        label = "alphaTarjeta"
    )
    val desplazamientoTarjeta by animateFloatAsState(
        targetValue = if (iniciarAnimacion) 0f else 60f,
        animationSpec = tween(durationMillis = 700, delayMillis = 150, easing = FastOutSlowInEasing),
        label = "desplazamientoTarjeta"
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // =========================================================================
            // 1. CABECERA HERO + LOGO GRANDE EN TODA LA ZONA DERECHA
            // =========================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banner_login),
                    contentDescription = "Banner Induztek",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0B1F3A).copy(alpha = 0.85f),
                                    Color(0xFF153B6E).copy(alpha = 0.96f)
                                )
                            )
                        )
                )

                // Logo limpio sin doble marco blanco
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .statusBarsPadding()
                        .padding(end = Dimens.espacioGrande, bottom = 20.dp)
                        .size(112.dp)
                        .graphicsLayer {
                            alpha = alphaCabecera
                            translationY = desplazamientoCabecera
                        }
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_induztek),
                        contentDescription = "Logo Induztek",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = 1.38f
                                scaleY = 1.38f
                                translationX = 8f
                                translationY = 8f
                            }
                    )
                }

                // Texto de marca en la parte inferior izquierda (con espacio a la derecha para no chocar)
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            start = Dimens.espacioGrande,
                            end = 148.dp, // Reserva el ancho del logo derecho para que no se monten
                            bottom = 42.dp
                        )
                        .graphicsLayer {
                            alpha = alphaCabecera
                            translationY = desplazamientoCabecera
                        }
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_giro),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PLATAFORMA DE TERRENO",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "INDUZTEK",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        text = "Gestión de Pruebas en Terreno",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // =========================================================================
            // 2. TARJETA DE LOGIN
            // =========================================================================
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp)
                    .padding(horizontal = Dimens.espacioGrande)
                    .graphicsLayer {
                        alpha = alphaTarjeta
                        translationY = desplazamientoTarjeta
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Bienvenido",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Ingresa tus credenciales para continuar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AnimatedVisibility(visible = !uiState.errorGeneral.isNullOrBlank()) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.errorGeneral ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    CampoTexto(
                        valor = uiState.correo,
                        onValorCambia = onCorreoCambia,
                        etiqueta = "Correo Electrónico",
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

                    TextButton(
                        onClick = onNavegarRecuperarContrasena,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    BotonPrincipal(
                        texto = "Iniciar Sesión",
                        onClick = onIngresar,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )
                }
            }


            // 3 pie de pag
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-12).dp)
                    .padding(bottom = Dimens.espacioGrande)
            ) {
                TextButton(onClick = { mostrarEmergente = true }) {
                    Text(
                        text = "Acerca de Induztek v1.0",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (mostrarEmergente) {
        AlertDialog(
            onDismissRequest = { mostrarEmergente = false },
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "Información de la App",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Aplicación Induztek v1.0\nDesarrollada para el control y gestión de protocolos de pruebas técnicas en terreno.")
            },
            confirmButton = {
                TextButton(onClick = { mostrarEmergente = false }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarEmergente = false }) {
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