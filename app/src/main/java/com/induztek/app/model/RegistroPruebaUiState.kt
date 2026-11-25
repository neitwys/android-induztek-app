package com.induztek.app.model

data class RegistroPruebaUiState(
    val listaEquipos: List<Equipo> = emptyList(),
    val idEquipoSeleccionado: Long = 1L,
    val tipoPrueba: String = "",
    val unidadMedida: String = "MΩ",
    val valorMedicion: String = "",
    val observaciones: String = "",
    val fotoUri: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val direccionGps: String? = null,
    val errorTipoPrueba: String? = null,
    val errorValorMedicion: String? = null,
    val registroExitoso: Boolean = false
)