package com.induztek.app.data.repository

import com.induztek.app.data.remote.ActualizarPerfilRequest
import com.induztek.app.data.remote.AutenticacionApi
import com.induztek.app.data.remote.CambiarContrasenaRequest
import com.induztek.app.data.remote.RecuperarContrasenaRequest
import com.induztek.app.data.remote.RegistroUsuarioRequest
import com.induztek.app.data.remote.RetrofitClient
import com.induztek.app.data.remote.aUsuario
import com.induztek.app.model.Usuario
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class RemoteUsuarioRepository(
    private val api: AutenticacionApi = RetrofitClient.autenticacionApi
) : UsuarioRepository {

    override suspend fun buscarPorId(id: Long): Usuario =
        llamar { api.buscarPorId(id) }.aUsuario()

    override suspend fun listarUsuarios(): List<Usuario> =
        llamar { api.listar() }.map { it.aUsuario() }

    override suspend fun eliminarUsuario(idUsuario: Long) =
        llamar { api.eliminar(idUsuario) }

    override suspend fun registrarUsuario(
        nombre: String,
        correo: String,
        contrasenaPlana: String,
        tipoPregunta: Int,
        respuestaPregunta: String,
        rolId: Long,
        empresaId: Long?
    ): Usuario {
        val datos = RegistroUsuarioRequest(
            nombre = nombre.trim(),
            correo = correo.trim(),
            contrasena = contrasenaPlana.trim(),
            rol = rolId,
            tipoPreguntaClave = tipoPregunta,
            respuestaPreguntaClave = respuestaPregunta.trim(),
            empresaId = empresaId
        )
        return llamar { api.crear(datos) }.aUsuario()
    }

    override suspend fun actualizarDatosPerfil(
        idUsuario: Long,
        nombre: String,
        correo: String
    ): Usuario {
        val datos = ActualizarPerfilRequest(
            nombre = nombre.trim(),
            correo = correo.trim()
        )
        return llamar { api.actualizarPerfil(idUsuario, datos) }.aUsuario()
    }

    override suspend fun cambiarContrasena(
        idUsuario: Long,
        contrasenaActualPlana: String,
        respuestaSeguridad: String,
        nuevaContrasenaPlana: String
    ) {
        val datos = CambiarContrasenaRequest(
            contrasenaActual = contrasenaActualPlana.trim(),
            respuestaSeguridad = respuestaSeguridad.trim(),
            nuevaContrasena = nuevaContrasenaPlana.trim()
        )
        llamar { api.cambiarContrasena(idUsuario, datos) }
    }

    override suspend fun obtenerPreguntaSeguridad(correo: String): Int =
        llamar { api.obtenerPregunta(correo.trim()) }.tipoPreguntaClave

    override suspend fun recuperarContrasena(
        correo: String,
        respuestaSeguridad: String,
        nuevaContrasenaPlana: String
    ) {
        val datos = RecuperarContrasenaRequest(
            correo = correo.trim(),
            respuestaSeguridad = respuestaSeguridad.trim(),
            nuevaContrasena = nuevaContrasenaPlana.trim()
        )
        llamar { api.recuperarContrasena(datos) }
    }

    private suspend fun <T> llamar(peticion: suspend () -> T): T = try {
        peticion()
    } catch (e: HttpException) {
        throw Exception(mensajeDelServidor(e))
    } catch (e: IOException) {
        throw Exception("No se pudo conectar con el servidor")
    }

    private fun mensajeDelServidor(e: HttpException): String {
        val cuerpo = e.response()?.errorBody()?.string()
        val mensaje = try {
            JSONObject(cuerpo ?: "").optString("message")
        } catch (ex: Exception) {
            ""
        }
        return mensaje.ifBlank { "Error del servidor (${e.code()})" }
    }
}