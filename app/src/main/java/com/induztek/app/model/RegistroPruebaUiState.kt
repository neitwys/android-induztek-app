package com.induztek.app.model

data class RegistroPruebaUiState(
    val listaEquipos: List<Equipo> = emptyList(),
    val idEquipoSeleccionado: Long = 0L,
    val tipoPrueba: String = "Resistencia de Aislamiento (MΩ)",
    val valorMedicion: String = "",
    val resultado: String = "APROBADO",
    val observaciones: String = "",
    val fotoUri: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val errorValorMedicion: String? = null,
    val registroExitoso: Boolean = false
)
