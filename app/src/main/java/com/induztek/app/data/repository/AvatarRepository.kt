package com.induztek.app.data.repository

import com.induztek.app.data.remote.AvatarApi
import com.induztek.app.data.remote.RetrofitClient
import com.induztek.app.data.remote.aUsuario
import com.induztek.app.model.Usuario
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

class AvatarRepository(
    private val avatarApi: AvatarApi = RetrofitClient.avatarApi
) {

    suspend fun subir(idUsuario: Long, bytes: ByteArray): Usuario {
        val cuerpo = bytes.toRequestBody("image/jpeg".toMediaType())
        val parte = MultipartBody.Part.createFormData("archivo", "avatar.jpg", cuerpo)
        return try {
            avatarApi.subir(idUsuario, parte).aUsuario()
        } catch (e: HttpException) {
            throw Exception("No se pudo subir la foto (error ${e.code()})")
        } catch (e: IOException) {
            throw Exception("No se pudo conectar con el servidor")
        }
    }
}