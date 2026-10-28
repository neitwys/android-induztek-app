package com.induztek.app.model

data class RegistroUsuarioEmpresaUiState(
    val empresaId: Long = 0L,
    val empresaNombre: String = "",
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val tipoPreguntaClave: Int = 1,
    val respuestaPreguntaClave: String = "",

    // Validaciones
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorRespuestaPregunta: String? = null,

    val registroExitoso: Boolean = false,

    val contrasenaVisible: Boolean = false
)