package com.induztek.app.model

data class RevisionPruebasUiState(
    val pruebas: List<ProtocoloPrueba> = emptyList(),
    val filtroEstado: String = "PENDIENTE_APROBACION", // "PENDIENTE_APROBACION", "APROBADA", "RECHAZADA", "TODAS"
    val pruebaSeleccionada: ProtocoloPrueba? = null,
    val motivoRechazo: String = "",
    val cargando: Boolean = false,
    val mensajeEstado: String? = null,
    val errorMensaje: String? = null
)