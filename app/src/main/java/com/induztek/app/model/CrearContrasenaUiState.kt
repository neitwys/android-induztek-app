package com.induztek.app.model

data class CrearContrasenaUiState(
    val nuevaContrasena: String = "",
    val confirmarContrasena: String = "",
    val errorContrasena: String? = null,
    val errorConfirmar: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    val contrasenaVisible: Boolean = false
)