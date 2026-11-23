package com.induztek.app.data.repository

import com.induztek.app.model.Usuario

interface UsuarioRepository {
    suspend fun buscarPorId(id: Long): Usuario
    suspend fun listarUsuarios(): List<Usuario>
    suspend fun eliminarUsuario(idUsuario: Long)

    suspend fun deshabilitarUsuario(usuarioId: Long)
    suspend fun registrarUsuario(
        nombre: String,
        correo: String,
        contrasenaPlana: String,
        rolId: Long,
        empresaId: Long? = null
    ): Usuario
    suspend fun actualizarDatosPerfil(idUsuario: Long, nombre: String, correo: String): Usuario
    suspend fun cambiarContrasena(
        idUsuario: Long,
        contrasenaActualPlana: String,
        nuevaContrasenaPlana: String
    )
    suspend fun solicitarCodigoRecuperacion(correo: String)
    suspend fun recuperarContrasena(
        correo: String,
        codigo: String,
        nuevaContrasenaPlana: String
    )
}