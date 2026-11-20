package com.induztek.app.data.repository

import com.induztek.app.model.Usuario

interface UsuarioRepository {
    suspend fun buscarPorId(id: Long): Usuario
    suspend fun listarUsuarios(): List<Usuario>
    suspend fun eliminarUsuario(idUsuario: Long)
    suspend fun registrarUsuario(
        nombre: String,
        correo: String,
        contrasenaPlana: String,
        tipoPregunta: Int,
        respuestaPregunta: String,
        rolId: Long,
        empresaId: Long? = null
    ): Usuario
    suspend fun actualizarDatosPerfil(idUsuario: Long, nombre: String, correo: String): Usuario
    suspend fun cambiarContrasena(
        idUsuario: Long,
        contrasenaActualPlana: String,
        respuestaSeguridad: String,
        nuevaContrasenaPlana: String
    )
    suspend fun obtenerPreguntaSeguridad(correo: String): Int
    suspend fun recuperarContrasena(
        correo: String,
        respuestaSeguridad: String,
        nuevaContrasenaPlana: String
    )
}