package com.induztek.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.induztek.app.data.repository.LocalPruebaRepository
import com.induztek.app.data.repository.PruebaRepository
import com.induztek.app.model.HistorialEquiposUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HistorialEquiposViewModel(
    application: Application,
    private val pruebaRepository: PruebaRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        pruebaRepository = LocalPruebaRepository(application)
    )

    private val _uiState = MutableStateFlow(HistorialEquiposUiState())
    val uiState: StateFlow<HistorialEquiposUiState> = _uiState.asStateFlow()

    private var empresaIdFiltro: Long? = null

    fun configurarFiltroEmpresa(empresaId: Long?) {
        empresaIdFiltro = empresaId
        cargarHistorial(_uiState.value.busquedaCodigo)
    }

    fun onBusquedaCambia(nuevoTexto: String) {
        _uiState.update { it.copy(busquedaCodigo = nuevoTexto) }
        cargarHistorial(nuevoTexto)
    }

    fun cargarHistorial(codigoEquipo: String) {
        _uiState.update { it.copy(cargando = true) }
        val lista = pruebaRepository.obtenerProtocolosPorEquipo(codigoEquipo, empresaIdFiltro)
        _uiState.update { it.copy(listaProtocolos = lista, cargando = false) }
    }
}