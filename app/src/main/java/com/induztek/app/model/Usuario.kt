package com.induztek.app.model

data class Usuario(
    val id: Long = 0L,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rolId: Long,
    val rolNombre: String = "",
    val empresaCliente: String? = null,
    val fotoPerfilPath: String? = null // <- Asegúrate de tener esta línea
)