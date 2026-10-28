package com.induztek.app.model

data class RegistroEmpresaUiState(
    // Datos de la Empresa
    val razonSocial: String = "",
    val rut: String = "",
    val giro: String = "", //Esto se refiere a que se especializa, Minería, Salud, etc.
    val contacto: String = "",
    val telefono: String = "",
    val correo: String = "",

    // Datos de la instalación
    val nombreInstalacion: String = "",
    val ubicacion: String = "",
    val comuna: String = "",
    val region: String = "",
    val observacionesAcceso: String = "", // EJ: requiere inducción de seguridad", "sin señal en sala eléctrica"

    // Validaciones
    val errorRazonSocial: String? = null,
    val errorRut: String? = null,
    val errorGiro: String? = null,
    val errorContacto: String? = null,
    val errorTelefono: String? = null,
    val errorCorreo: String? = null,

    val errorNombreInstalacion: String? = null,
    val errorUbicacion: String? = null,
    val errorComuna: String? = null,
    val errorRegion: String? = null,
    val errorObservacionesAcceso: String? = null,

    // Resultado del formulario
    val idEmpresaGenerado: Long? = null,
    val registroExitoso: Boolean = false
)
