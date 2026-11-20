package com.induztek.app.model

data class RecuperarContrasenaUiState(
    val correo: String = "",
    val preguntaSeguridad: String? = null,
    val respuestaPregunta: String = "",
    val nuevaContrasena: String = "",
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)