package com.induztek.app.model

enum class RolUsuario(val etiqueta: String) {
    TECNICO("Técnico"),
    INGENIERO("Ingeniero de Pruebas"),
    JEFE_TECNICO("Jefe Técnico"),
    CLIENTE("Cliente")
}

data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val rol: RolUsuario,
    val empresaCliente: String? = null //Por si el rol es Cliente, verificar su Instalacion.
)

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false
)

