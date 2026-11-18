package com.induztek.app.data.repository

import android.content.Context
import com.induztek.app.data.InduztekDbHelper
import com.induztek.app.model.Equipo
import com.induztek.app.model.ProtocoloPrueba

interface PruebaRepository {
    fun listarEquipos(): List<Equipo>
    fun registrarProtocolo(protocolo: ProtocoloPrueba): Boolean
    fun obtenerProtocolosPorEquipo(codigoEquipo: String, empresaIdFiltro: Long? = null): List<ProtocoloPrueba>
}

class LocalPruebaRepository(context: Context) : PruebaRepository {
    private val dbHelper = InduztekDbHelper(context.applicationContext)

    override fun listarEquipos(): List<Equipo> = dbHelper.listarEquipos()

    override fun registrarProtocolo(protocolo: ProtocoloPrueba): Boolean =
        dbHelper.insertarProtocolo(protocolo)

    override fun obtenerProtocolosPorEquipo(codigoEquipo: String, empresaIdFiltro: Long?): List<ProtocoloPrueba> =
        dbHelper.obtenerProtocolosPorEquipo(codigoEquipo, empresaIdFiltro)
}