package com.induztek.app.data.remote

import com.induztek.app.model.ProtocoloPrueba
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class MotivoRechazoRequest(
    val motivo: String
)

interface PruebaApiService {

    @GET("api/pruebas")
    suspend fun listarPruebas(
        @Query("estado") estado: String? = null,
        @Query("idEquipo") idEquipo: Long? = null,
        @Query("codigoEquipo") codigoEquipo: String? = null
    ): Response<List<ProtocoloPrueba>>

    @GET("api/pruebas/{id}")
    suspend fun obtenerPorId(@Path("id") id: Long): Response<ProtocoloPrueba>

    @POST("api/pruebas")
    suspend fun crearPrueba(@Body protocolo: ProtocoloPrueba): Response<ProtocoloPrueba>

    @PUT("api/pruebas/{id}")
    suspend fun actualizarPrueba(
        @Path("id") id: Long,
        @Body protocolo: ProtocoloPrueba
    ): Response<ProtocoloPrueba>

    @PATCH("api/pruebas/{id}/aprobar")
    suspend fun aprobarPrueba(
        @Path("id") id: Long,
        @Query("idUsuarioIngeniero") idUsuarioIngeniero: Long
    ): Response<ProtocoloPrueba>

    @PATCH("api/pruebas/{id}/rechazar")
    suspend fun rechazarPrueba(
        @Path("id") id: Long,
        @Query("idUsuarioIngeniero") idUsuarioIngeniero: Long,
        @Body body: MotivoRechazoRequest
    ): Response<ProtocoloPrueba>
}