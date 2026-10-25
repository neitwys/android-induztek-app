package com.induztek.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.induztek.app.data.InduztekDbHelper
import com.induztek.app.model.HistorialEquiposUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HistorialEquiposViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = InduztekDbHelper(application)

    private val _uiState = MutableStateFlow(HistorialEquiposUiState())
    val uiState: StateFlow<HistorialEquiposUiState> = _uiState.asStateFlow()

    init {
        cargarHistorial("")
    }

    fun onBusquedaCambia(nuevoTexto: String) {
        _uiState.update { it.copy(busquedaCodigo = nuevoTexto) }
        cargarHistorial(nuevoTexto)
    }

    fun cargarHistorial(codigoEquipo: String) {
        _uiState.update { it.copy(cargando = true) }
        val lista = dbHelper.obtenerProtocolosPorEquipo(codigoEquipo)
        _uiState.update { it.copy(listaProtocolos = lista, cargando = false) }
    }
}