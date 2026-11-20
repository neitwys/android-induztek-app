package com.induztek.app.data.remote

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AvatarApi {

    @Multipart
    @POST("api/autenticacion/{id}/avatar")
    suspend fun subir(
        @Path("id") id: Long,
        @Part archivo: MultipartBody.Part
    ): UsuarioResponse
}