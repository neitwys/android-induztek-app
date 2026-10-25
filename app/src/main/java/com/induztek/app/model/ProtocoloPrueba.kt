package com.induztek.app.model

data class ProtocoloPrueba(
    val idProtocolo: Long = 0,
    val idEquipo: Long,
    val codigoEquipo: String = "",
    val tipoEquipo: String = "",
    val idUsuarioTecnico: Long,
    val nombreTecnico: String = "",
    val fechaHora: String,
    val tipoPrueba: String,
    val valorMedicion: String,
    val resultado: String, // "APROBADO" o "RECHAZADO"
    val estadoProtocolo: String = "REGISTRADO_TERRENO", // "REGISTRADO_TERRENO" o "FORMALIZADO"
    val observaciones: String = "",
    val fotoUri: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)