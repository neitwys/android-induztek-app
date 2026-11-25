package com.induztek.app.data.repository

import android.content.Context
import com.induztek.app.data.InduztekDbHelper
import com.induztek.app.data.remote.MotivoRechazoRequest
import com.induztek.app.data.remote.PruebaApiService
import com.induztek.app.data.remote.RetrofitClient
import com.induztek.app.model.Equipo
import com.induztek.app.model.ProtocoloPrueba
import kotlinx.coroutines.runBlocking

interface PruebaRepository {
    fun listarEquipos(): List<Equipo>
    fun registrarProtocolo(protocolo: ProtocoloPrueba): Boolean
    fun obtenerProtocolosPorEquipo(codigoEquipo: String, empresaIdFiltro: Long? = null): List<ProtocoloPrueba>
    fun listarProtocolos(): List<ProtocoloPrueba>
    fun actualizarProtocolo(protocolo: ProtocoloPrueba): Boolean
}

class LocalPruebaRepository(context: Context) : PruebaRepository {
    private val dbHelper = InduztekDbHelper(context.applicationContext)
    private val revisionesLocales = mutableMapOf<String, ProtocoloPrueba>()

    override fun listarEquipos(): List<Equipo> = dbHelper.listarEquipos()

    override fun registrarProtocolo(protocolo: ProtocoloPrueba): Boolean =
        dbHelper.insertarProtocolo(protocolo)

    override fun obtenerProtocolosPorEquipo(codigoEquipo: String, empresaIdFiltro: Long?): List<ProtocoloPrueba> =
        dbHelper.obtenerProtocolosPorEquipo(codigoEquipo, empresaIdFiltro)

    override fun listarProtocolos(): List<ProtocoloPrueba> {
        val equipos = dbHelper.listarEquipos()
        val protocolos = equipos.flatMap { equipo ->
            dbHelper.obtenerProtocolosPorEquipo(equipo.codigoEquipo, null)
        }
        return protocolos.map { item ->
            val clave = claveProtocolo(item)
            revisionesLocales[clave] ?: item
        }
    }

    override fun actualizarProtocolo(protocolo: ProtocoloPrueba): Boolean {
        val clave = claveProtocolo(protocolo)
        revisionesLocales[clave] = protocolo
        return true
    }

    private fun claveProtocolo(p: ProtocoloPrueba): String =
        "${p.idEquipo}_${p.idUsuarioTecnico}_${p.fechaHora}_${p.tipoPrueba}"
}

class RemotePruebaRepository(
    context: Context,
    private val api: PruebaApiService = RetrofitClient.pruebaApi
) : PruebaRepository {

    private val dbHelper = InduztekDbHelper(context.applicationContext)

    override fun listarEquipos(): List<Equipo> = dbHelper.listarEquipos()

    override fun registrarProtocolo(protocolo: ProtocoloPrueba): Boolean = runBlocking {
        try {
            val response = api.crearPrueba(protocolo)
            if (response.isSuccessful) {
                val guardado = response.body() ?: protocolo
                dbHelper.insertarProtocolo(guardado)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun obtenerProtocolosPorEquipo(
        codigoEquipo: String,
        empresaIdFiltro: Long?
    ): List<ProtocoloPrueba> = runBlocking {
        try {
            val response = api.listarPruebas(codigoEquipo = codigoEquipo)
            if (response.isSuccessful) {
                val remotos = response.body().orEmpty()
                if (empresaIdFiltro == null) {
                    remotos
                } else {
                    val localesFiltrados = dbHelper.obtenerProtocolosPorEquipo(codigoEquipo, empresaIdFiltro)
                    remotos.ifEmpty { localesFiltrados }
                }
            } else {
                dbHelper.obtenerProtocolosPorEquipo(codigoEquipo, empresaIdFiltro)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            dbHelper.obtenerProtocolosPorEquipo(codigoEquipo, empresaIdFiltro)
        }
    }

    override fun listarProtocolos(): List<ProtocoloPrueba> = runBlocking {
        try {
            val response = api.listarPruebas()
            if (response.isSuccessful) {
                response.body().orEmpty()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun actualizarProtocolo(protocolo: ProtocoloPrueba): Boolean = runBlocking {
        try {
            val idIngeniero = protocolo.idUsuarioIngeniero ?: 1L
            val estado = protocolo.estadoProtocolo.uppercase()
            val resultado = protocolo.resultado.uppercase()

            val response = when {
                estado == "APROBADA" || resultado == "APROBADO" -> {
                    api.aprobarPrueba(protocolo.idProtocolo, idIngeniero)
                }
                estado == "RECHAZADA" || resultado == "RECHAZADO" -> {
                    val motivo = protocolo.motivoRechazo?.takeIf { it.isNotBlank() }
                        ?: protocolo.observaciones.takeIf { it.isNotBlank() }
                        ?: "Rechazado por ingeniero"
                    api.rechazarPrueba(
                        id = protocolo.idProtocolo,
                        idUsuarioIngeniero = idIngeniero,
                        body = MotivoRechazoRequest(motivo)
                    )
                }
                else -> {
                    api.actualizarPrueba(protocolo.idProtocolo, protocolo)
                }
            }
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}