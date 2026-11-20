package com.induztek.app.data.repository

import retrofit2.HttpException
import com.induztek.app.data.remote.AutenticacionApi
import com.induztek.app.data.remote.LoginRequest
import com.induztek.app.data.remote.RetrofitClient
import com.induztek.app.data.remote.aUsuario
import com.induztek.app.model.Usuario
import org.json.JSONObject
import java.io.IOException

class RemoteAuthRepository(
    private val api: AutenticacionApi = RetrofitClient.autenticacionApi
) : AuthRepository {

    override suspend fun autenticar(correo: String, contrasenaPlana: String): Usuario =
        llamar {
            api.login(LoginRequest(correo = correo.trim(), contrasena = contrasenaPlana.trim()))
        }.aUsuario()

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