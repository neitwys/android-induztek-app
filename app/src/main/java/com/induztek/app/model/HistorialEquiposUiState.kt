package com.induztek.app.model

data class HistorialEquiposUiState(
    val busquedaCodigo: String = "",
    val listaProtocolos: List<ProtocoloPrueba> = emptyList(),
    val cargando: Boolean = false
)