package com.induztek.app.model

data class RecuperarContrasenaUiState(
    val correo: String = "",
    val codigoEnviado: Boolean = false,
    val codigo: String = "",
    val nuevaContrasena: String = "",
    val cargando: Boolean = false,
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)