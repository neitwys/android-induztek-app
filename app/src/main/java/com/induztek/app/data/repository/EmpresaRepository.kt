package com.induztek.app.data.repository

import android.content.Context
import com.induztek.app.data.EmpresaConInstalacion
import com.induztek.app.data.InduztekDbHelper
import com.induztek.app.data.remote.EmpresaResponse

interface EmpresaRepository {
    suspend fun registrarEmpresaConInstalacion(
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
    ): Result<Long>

    suspend fun listarEmpresas(): List<EmpresaConInstalacion>

    suspend fun eliminarEmpresa(id: Long): Boolean

    suspend fun obtenerEmpresaPorId(id: Long): Result<EmpresaResponse>
}