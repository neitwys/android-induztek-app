package com.induztek.app.data.remote

import retrofit2.Response
import retrofit2.http.*

interface ClienteApi {

    @GET("api/empresas")
    suspend fun listarEmpresas(): Response<List<EmpresaResponse>>

    @GET("api/empresas/{id}")
    suspend fun obtenerEmpresaPorId(@Path("id") id: Long): Response<EmpresaResponse>

    @POST("api/empresas")
    suspend fun crearEmpresa(@Body request: CrearEmpresaRequest): Response<EmpresaResponse>

    @DELETE("api/empresas/{id}")
    suspend fun eliminarEmpresa(@Path("id") id: Long): Response<Void>

}