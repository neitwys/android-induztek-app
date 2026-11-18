package com.induztek.app.model

data class RegistroPruebaUiState(
    val listaEquipos: List<Equipo> = emptyList(),
    val idEquipoSeleccionado: Long = 1L,
    val tipoPrueba: String = "Resistencia de Aislación",
    val errorTipoPrueba: String? = null,
    val valorMedicion: String = "",
    val errorValorMedicion: String? = null,
    val unidadMedida: String = "MΩ",
    val resultado: String = "APROBADO",
    val observaciones: String = "",
    val fotoUri: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val registroExitoso: Boolean = false
)