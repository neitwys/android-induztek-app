package com.induztek.app.data.repository

import android.content.Context
import com.induztek.app.data.InduztekDbHelper

interface EmpresaRepository {
    fun registrarEmpresaConInstalacion(
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
    ): Long
}

class LocalEmpresaRepository(context: Context) : EmpresaRepository {
    private val dbHelper = InduztekDbHelper(context.applicationContext)

    override fun registrarEmpresaConInstalacion(
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
    ): Long {
        return dbHelper.insertarEmpresaConInstalacion(
            razonSocial = razonSocial,
            rut = rut,
            giro = giro,
            contacto = contacto,
            telefono = telefono,
            correo = correo,
            nombreInstalacion = nombreInstalacion,
            ubicacion = ubicacion,
            comuna = comuna,
            region = region,
            observaciones = observaciones
        )
    }
}