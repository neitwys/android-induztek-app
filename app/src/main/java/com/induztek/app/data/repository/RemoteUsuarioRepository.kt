package com.induztek.app.data.repository

import com.induztek.app.data.remote.ActualizarPerfilRequest
import com.induztek.app.data.remote.AutenticacionApi
import com.induztek.app.data.remote.CambiarContrasenaRequest
import com.induztek.app.data.remote.CrearPrimeraContrasenaRequest
import com.induztek.app.data.remote.RecuperarContrasenaRequest
import com.induztek.app.data.remote.RegistroUsuarioRequest
import com.induztek.app.data.remote.RetrofitClient
import com.induztek.app.data.remote.SolicitarCodigoRequest
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
    override suspend fun deshabilitarUsuario(usuarioId: Long) {

        llamar { api.deshabilitar(usuarioId) }
    }

    override suspend fun registrarUsuario(
        nombre: String,
        correo: String,
        contrasenaPlana: String,
        rolId: Long,
        empresaId: Long?
    ): Usuario {
        val datos = RegistroUsuarioRequest(
            nombre = nombre.trim(),
            correo = correo.trim(),
            contrasena = contrasenaPlana.trim(),
            rol = rolId,
            empresaId = empresaId
        )
        return llamar { api.crear(datos) }.aUsuario()
    }

    override suspend fun actualizarDatosPerfil(idUsuario: Long, nombre: String, correo: String): Usuario =
        llamar { api.actualizarPerfil(idUsuario, ActualizarPerfilRequest(nombre.trim(), correo.trim())) }.aUsuario()

    override suspend fun cambiarContrasena(
        idUsuario: Long,
        contrasenaActualPlana: String,
        nuevaContrasenaPlana: String
    ) {
        llamar {
            api.cambiarContrasena(
                idUsuario,
                CambiarContrasenaRequest(contrasenaActualPlana.trim(), nuevaContrasenaPlana.trim())
            )
        }
    }

    override suspend fun solicitarCodigoRecuperacion(correo: String) {
        llamar { api.solicitarCodigo(SolicitarCodigoRequest(correo.trim())) }
    }

    override suspend fun recuperarContrasena(correo: String, codigo: String, nuevaContrasenaPlana: String) {
        llamar {
            api.recuperarContrasena(
                RecuperarContrasenaRequest(correo.trim(), codigo.trim(), nuevaContrasenaPlana.trim())
            )
        }
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
            val json = JSONObject(cuerpo ?: "")
            json.optString("error").ifBlank { json.optString("message") }
        } catch (ex: Exception) {
            ""
        }
        return mensaje.ifBlank { "Error del servidor (${e.code()})" }
    }

    override suspend fun crearPrimeraContrasena(idUsuario: Long, nuevaContrasenaPlana: String) {
        llamar {
            api.crearPrimeraContrasena(
                idUsuario,
                CrearPrimeraContrasenaRequest(nuevaContrasenaPlana.trim())
            )
        }
    }
}