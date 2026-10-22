package com.induztek.app.model

data class Usuario(
    val nombre: String,
    val correo: String,
    val contrasena: String = "",
    val id: Long = 0,                   //Clave primaria de la fila, 0 significa que todavia no se guarda en la DB.
    val rolId: Long = Rol.ID_DEFAULT,   //Clave foranea, el rol por defecto.
    val rolNombre: String = "",         //Nombre del rol, va a llegar con un JOIN.
    val empresaCliente: String? = null  //Empresa asociada, SI es que el ROL es ID_CLIENTE.
)
