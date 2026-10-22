package com.induztek.app.model

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,

    val contrasenaVisible: Boolean = false
)

