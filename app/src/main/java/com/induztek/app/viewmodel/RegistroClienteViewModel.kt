package com.induztek.app.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.induztek.app.data.InduztekDbHelper
import com.induztek.app.model.RegistroClienteUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

//Aqui tambien cambiamos al AndroidViewModel porque usa las base de datos.
class RegistroClienteViewModel(application: Application): AndroidViewModel(application) {
    private val dbHelper = InduztekDbHelper(application)
    private val _uiState = MutableStateFlow(RegistroClienteUiState())
    val uiState: StateFlow<RegistroClienteUiState> = _uiState.asStateFlow()

    // Eventos de los campos

    // Eventos del Cliente
    fun onRazonSocialCambia(valor: String) {
        _uiState.update { it.copy(razonSocial = valor, errorRazonSocial = null, registroExitoso = false) }
    }
    fun onRutCambia(valor: String) {
        _uiState.update { it.copy(rut = valor, errorRut = null, registroExitoso = false) }
    }
    fun onGiroCambia(valor: String) {
        _uiState.update { it.copy(giro = valor, errorGiro = null, registroExitoso = false) }
    }
    fun onContactoCambia(valor: String) {
        _uiState.update { it.copy(contacto = valor, errorContacto = null, registroExitoso = false) }
    }
    fun onTelefonoCambia(valor: String) {
        _uiState.update { it.copy(telefono = valor, errorTelefono = null, registroExitoso = false) }
    }
    fun onCorreoCambia(valor: String) {
        _uiState.update { it.copy(correo = valor, errorCorreo = null, registroExitoso = false) }
    }

    // Eventos de la instalación
    fun onNombreInstalacionCambia(valor: String) {
        _uiState.update { it.copy(nombreInstalacion = valor, errorNombreInstalacion = null, registroExitoso = false) }
    }
    fun onUbicacionCambia(valor: String) {
        _uiState.update { it.copy(ubicacion = valor, errorUbicacion = null, registroExitoso = false) }
    }
    fun onComunaCambia(valor: String) {
        _uiState.update { it.copy(comuna = valor, errorComuna = null, registroExitoso = false) }
    }
    fun onRegionCambia(valor: String) {
        _uiState.update { it.copy(region = valor, errorRegion = null, registroExitoso = false) }
    }
    fun onObservacionesAccesoCambia(valor: String) {
        _uiState.update { it.copy(observacionesAcceso = valor, errorObservacionesAcceso = null, registroExitoso = false) }
    }

    // Validaciones para botón registrarse
    private fun validarRazonSocial(razonSocial: String): String? {
        return if (razonSocial.isBlank()) "La razón social es obligatoria" else null
    }
    private fun validarRut(rut: String): String? {
        return if (rut.isBlank()) "El RUT es obligatorio" else null
    }

    private fun validarContacto(contacto: String): String? {
        return if (contacto.isBlank()) "El nombre de contacto es obligatorio" else null
    }
    private fun validarCorreo(correo: String): String? {
        if (correo.isBlank()) return null
        val esValido = Patterns.EMAIL_ADDRESS.matcher(correo).matches()
        return if (!esValido) "Ingrese un correo válido (ej: usuario@dominio.cl)" else null
    }
    private fun validarNombreInstalacion(nombre: String): String? {
        return if (nombre.isBlank()) "El nombre de la instalación es obligatorio" else null
    }

    // Botones

    // Botón limpiar
    fun onLimpiar() {
        _uiState.value = RegistroClienteUiState()
    }

    // Botón registrarse
    fun onRegistrar() {
        val estado = _uiState.value

        val errorRazonSocial = validarRazonSocial(estado.razonSocial)
        val errorRut = validarRut(estado.rut)
        val errorCorreo = validarCorreo(estado.correo)
        val errorContacto = validarContacto(estado.contacto)
        val errorNombreInstalacion = validarNombreInstalacion(estado.nombreInstalacion)

        val hayErrores = listOf(
            errorRazonSocial,
            errorRut,
            errorCorreo,
            errorContacto,
            errorNombreInstalacion
        ).any { it != null }

        if (hayErrores) {
            _uiState.update { it.copy(
                errorRazonSocial = errorRazonSocial,
                errorRut = errorRut,
                errorCorreo = errorCorreo,
                errorContacto = errorContacto,
                errorNombreInstalacion = errorNombreInstalacion,
                registroExitoso = false
            ) }
            return
        }

        //Si pasa las validaciones, guardamos en la base de datos.
        val exitoDb = dbHelper.insertarClienteConInstalacion(
            razonSocial = estado.razonSocial,
            rut = estado.rut,
            giro = estado.giro,
            contacto = estado.contacto,
            telefono = estado.telefono,
            correo = estado.correo,
            nombreInstalacion = estado.nombreInstalacion,
            ubicacion = estado.ubicacion,
            comuna = estado.comuna,
            region = estado.region,
            observaciones = estado.observacionesAcceso
        )


        _uiState.update { it.copy(
            errorRazonSocial = errorRazonSocial,
            errorRut = errorRut,
            errorCorreo = errorCorreo,
            errorContacto = errorContacto,
            errorNombreInstalacion = errorNombreInstalacion,
            registroExitoso = exitoDb
        ) }
    }



}