package com.induztek.app.data.remote

data class ContactoRequest(
    val nombre: String,
    val telefono: String,
    val correo: String,
    val cargo: String = "Contacto Principal"
)

data class InstalacionRequest(
    val nombreInstalacion: String,
    val ubicacion: String,
    val comuna: String,
    val region: String,
    val observacionesAcceso: String
)

data class CrearEmpresaRequest(
    val razonSocial: String,
    val rut: String,
    val giro: String,
    val contactos: List<ContactoRequest>,
    val instalaciones: List<InstalacionRequest>
)

data class ContactoResponse(
    val id: Long,
    val nombre: String,
    val telefono: String?,
    val correo: String?,
    val cargo: String?
)

data class InstalacionResponse(
    val id: Long,
    val nombreInstalacion: String,
    val ubicacion: String?,
    val comuna: String?,
    val region: String?,
    val observacionesAcceso: String?
)

data class EmpresaResponse(
    val id: Long,
    val razonSocial: String,
    val rut: String,
    val giro: String?,
    val contactos: List<ContactoResponse>?,
    val instalaciones: List<InstalacionResponse>?
)