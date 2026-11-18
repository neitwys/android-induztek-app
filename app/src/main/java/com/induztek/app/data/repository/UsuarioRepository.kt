package com.induztek.app.data.repository

import android.content.Context
import com.induztek.app.data.InduztekDbHelper
import com.induztek.app.model.Usuario
import com.induztek.app.utils.SeguridadUtils

interface UsuarioRepository {
    fun buscarPorId(id: Long): Usuario?
    fun listarUsuarios(): List<Usuario>
    fun eliminarUsuario(idUsuario: Long): Boolean
    fun registrarUsuario(
        nombre: String,
        correo: String,
        contrasenaPlana: String,
        tipoPregunta: Int,
        respuestaPregunta: String,
        rolId: Int,
        empresaId: Long? = null
    ): Boolean
    fun actualizarDatosPerfil(idUsuario: Long, nombre: String, correo: String): Boolean
    fun actualizarFotoPerfil(idUsuario: Long, rutaFoto: String): Boolean
    fun cambiarContrasena(
        idUsuario: Long,
        contrasenaActualPlana: String,
        respuestaSeguridad: String,
        nuevaContrasenaPlana: String
    ): Result<Unit>
}

class LocalUsuarioRepository(context: Context) : UsuarioRepository {
    private val dbHelper = InduztekDbHelper(context.applicationContext)

    override fun buscarPorId(id: Long): Usuario? = dbHelper.buscarPorId(id)

    override fun listarUsuarios(): List<Usuario> = dbHelper.listarUsuarios()

    override fun eliminarUsuario(idUsuario: Long): Boolean = dbHelper.eliminarUsuario(idUsuario) > 0

    override fun registrarUsuario(
        nombre: String,
        correo: String,
        contrasenaPlana: String,
        tipoPregunta: Int,
        respuestaPregunta: String,
        rolId: Int,
        empresaId: Long?
    ): Boolean {
        return dbHelper.insertarUsuario(
            nombre = nombre,
            correo = correo,
            contrasena = contrasenaPlana,
            tipoPregunta = tipoPregunta,
            respuestaPregunta = respuestaPregunta,
            rolId = rolId,
            empresaId = empresaId
        )
    }

    override fun actualizarDatosPerfil(idUsuario: Long, nombre: String, correo: String): Boolean {
        return dbHelper.actualizarDatosUsuario(idUsuario, nombre.trim(), correo.trim().lowercase())
    }

    override fun actualizarFotoPerfil(idUsuario: Long, rutaFoto: String): Boolean {
        return dbHelper.actualizarFotoPerfilUsuario(idUsuario, rutaFoto)
    }

    override fun cambiarContrasena(
        idUsuario: Long,
        contrasenaActualPlana: String,
        respuestaSeguridad: String,
        nuevaContrasenaPlana: String
    ): Result<Unit> {
        val usuario = dbHelper.buscarPorId(idUsuario)
            ?: return Result.failure(IllegalStateException("Usuario no encontrado"))

        val hashActual = SeguridadUtils.hashSHA256(contrasenaActualPlana.trim())
        if (hashActual != usuario.contrasena) {
            return Result.failure(IllegalArgumentException("La contraseña actual es incorrecta"))
        }

        val respuestaNormalizada = respuestaSeguridad.trim().lowercase()
        val respuestaGuardada = usuario.respuestaPreguntaClave.trim().lowercase()
        if (respuestaGuardada.isNotBlank() && respuestaNormalizada != respuestaGuardada) {
            return Result.failure(IllegalArgumentException("La respuesta de seguridad no coincide"))
        }

        val filas = dbHelper.actualizarContrasena(idUsuario, nuevaContrasenaPlana)
        return if (filas > 0) Result.success(Unit)
        else Result.failure(IllegalStateException("No se pudo actualizar la contraseña"))
    }
}