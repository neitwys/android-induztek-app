package com.induztek.app.data.repository

import com.induztek.app.data.EmpresaConInstalacion
import com.induztek.app.data.remote.*

class RemoteEmpresaRepository(
    private val api: ClienteApi = RetrofitClient.clienteApi
) : EmpresaRepository {

    override suspend fun registrarEmpresaConInstalacion(
        razonSocial: String,
        rut: String,
        giro: String,
        contacto: String,
        telefono: String,
        correo: String,
        nombreInstalacion: String,
        ubicacion: String,
        comuna: String,
        region: String,
        observaciones: String
    ): Result<Long> {
        return try {
            val request = CrearEmpresaRequest(
                razonSocial = razonSocial,
                rut = rut,
                giro = giro,
                contactos = listOf(
                    ContactoRequest(
                        nombre = contacto,
                        telefono = telefono,
                        correo = correo
                    )
                ),
                instalaciones = listOf(
                    InstalacionRequest(
                        nombreInstalacion = nombreInstalacion,
                        ubicacion = ubicacion,
                        comuna = comuna,
                        region = region,
                        observacionesAcceso = observaciones
                    )
                )
            )

            val response = api.crearEmpresa(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.id)
            } else {
                // Analizamos el código de error HTTP
                when (response.code()) {
                    409, 500 -> Result.failure(Exception("El RUT ya se encuentra registrado."))
                    404 -> Result.failure(Exception("Error: Ruta no encontrada en API Gateway (HTTP 404)."))
                    else -> Result.failure(Exception("Error del servidor (HTTP ${response.code()})."))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error interno en la app: ${e.message}"))
        }
    }

    override suspend fun listarEmpresas(): List<EmpresaConInstalacion> {
        return try {
            val response = api.listarEmpresas()
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.map { emp ->
                    val primerContacto = emp.contactos?.firstOrNull()
                    val primeraInstalacion = emp.instalaciones?.firstOrNull()

                    EmpresaConInstalacion(
                        id = emp.id,
                        razonSocial = emp.razonSocial,
                        rut = emp.rut,
                        giro = emp.giro ?: "",
                        contacto = primerContacto?.nombre ?: "",
                        telefono = primerContacto?.telefono ?: "",
                        correo = primerContacto?.correo ?: "",
                        nombreInstalacion = primeraInstalacion?.nombreInstalacion ?: "Sin instalación",
                        ubicacion = primeraInstalacion?.ubicacion ?: "",
                        comuna = primeraInstalacion?.comuna ?: "",
                        region = primeraInstalacion?.region ?: ""
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun obtenerEmpresaPorId(id: Long): Result<EmpresaResponse> {
        return try {
            val response = api.obtenerEmpresaPorId(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar la empresa"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Sin conexión con el servidor"))
        }
    }

    override suspend fun eliminarEmpresa(id: Long): Boolean {
        return try {
            api.eliminarEmpresa(id).isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}