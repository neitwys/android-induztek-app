package com.induztek.app.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.Query
import retrofit2.http.Path

interface AutenticacionApi {

    @POST("api/autenticacion/crear")
    suspend fun crear(@Body datos: RegistroUsuarioRequest): UsuarioResponse

    @POST("api/autenticacion/login")
    suspend fun login(@Body datos: LoginRequest): UsuarioResponse

    @GET("api/autenticacion/listar")
    suspend fun listar(): List<UsuarioResponse>

    @GET("api/autenticacion/buscar/{id}")
    suspend fun buscarPorId(@Path("id") id: Long): UsuarioResponse

    @PUT("api/autenticacion/actualizar/{id}")
    suspend fun actualizarPerfil(
        @Path("id") id: Long,
        @Body datos: ActualizarPerfilRequest
    ): UsuarioResponse

    @PUT("api/autenticacion/{id}/contrasena")
    suspend fun cambiarContrasena(
        @Path("id") id: Long,
        @Body datos: CambiarContrasenaRequest
    )

    @POST("api/autenticacion/solicitar-codigo")
    suspend fun solicitarCodigo(@Body datos: SolicitarCodigoRequest)

    @PUT("api/autenticacion/recuperar-contrasena")
    suspend fun recuperarContrasena(@Body datos: RecuperarContrasenaRequest)

    @DELETE("api/autenticacion/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Long)

    @PATCH("api/autenticacion/usuarios/{id}/deshabilitar")
    suspend fun deshabilitar(@Path(value = "id") id: Long)
}