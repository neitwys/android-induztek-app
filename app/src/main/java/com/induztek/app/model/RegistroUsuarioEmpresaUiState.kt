package com.induztek.app.model

data class RegistroUsuarioEmpresaUiState(
    val empresaId: Long = 0L,
    val empresaNombre: String = "",
    val rolIdSeleccionado: Int = 4,
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",

    // Validaciones
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,

    val registroExitoso: Boolean = false,
    val contrasenaVisible: Boolean = false
)