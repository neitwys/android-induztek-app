package com.induztek.app.model

data class Equipo(
    val idEquipo: Long = 0,
    val codigoEquipo: String, // Ej: "TR-001"
    val tipoEquipo: String,   // Ej: "Transformador", "Relé", "Celda", "Cable"
    val idInstalacion: Long
)