package com.induztek.app.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.data.repository.EmpresaRepository
import com.induztek.app.data.repository.RemoteEmpresaRepository
import com.induztek.app.model.RegistroEmpresaUiState
import com.induztek.app.utils.ValidacionUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroEmpresaViewModel(
    application: Application,
    private val empresaRepository: EmpresaRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        empresaRepository = RemoteEmpresaRepository()
    )

    private val _uiState = MutableStateFlow(RegistroEmpresaUiState())
    val uiState: StateFlow<RegistroEmpresaUiState> = _uiState.asStateFlow()

    // Eventos de la Empresa
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
        _uiState.update {
            it.copy(region = valor, comuna = "", errorRegion = null, errorComuna = null, registroExitoso = false
            )
        }
    }

    fun onObservacionesAccesoCambia(valor: String) {
        _uiState.update { it.copy(observacionesAcceso = valor, errorObservacionesAcceso = null, registroExitoso = false) }
    }

    // Validaciones completas por campo
    private fun validarRazonSocial(valor: String): String? = when {
        valor.isBlank() -> "La razón social es obligatoria"
        valor.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        else -> null
    }

    private fun validarRut(valor: String): String? = when {
        valor.isBlank() -> "El RUT es obligatorio"
        !ValidacionUtils.esRutValido(valor) -> "RUT inválido (ej: 76.543.210-9)"
        else -> null
    }

    private fun validarGiro(valor: String): String? = when {
        valor.isBlank() -> "El giro comercial es obligatorio"
        valor.trim().length < 4 -> "Ingrese un giro descriptivo"
        else -> null
    }

    private fun validarContacto(valor: String): String? = when {
        valor.isBlank() -> "El nombre de contacto es obligatorio"
        valor.trim().length < 3 -> "Ingrese nombre y apellido"
        else -> null
    }

    private fun validarTelefono(valor: String): String? = when {
        valor.isBlank() -> "El teléfono es obligatorio"
        !ValidacionUtils.esTelefonoChilenoValido(valor) -> "Formato inválido (ej: +56912345678 o 912345678)"
        else -> null
    }

    private fun validarCorreo(valor: String): String? = when {
        valor.isBlank() -> "El correo corporativo es obligatorio"
        !Patterns.EMAIL_ADDRESS.matcher(valor.trim()).matches() -> "Ingrese un correo válido (ej: contacto@empresa.cl)"
        else -> null
    }

    private fun validarNombreInstalacion(valor: String): String? = when {
        valor.isBlank() -> "El nombre de la instalación es obligatorio"
        valor.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        else -> null
    }

    private fun validarUbicacion(valor: String): String? = when {
        valor.isBlank() -> "La dirección es obligatoria"
        valor.trim().length < 5 -> "Ingrese calle y numeración o referencia"
        else -> null
    }

    private fun validarComuna(valor: String): String? =
        if (valor.isBlank()) "La comuna es obligatoria" else null

    private fun validarRegion(valor: String): String? =
        if (valor.isBlank()) "La región es obligatoria" else null

    fun onLimpiar() {
        _uiState.value = RegistroEmpresaUiState()
    }

    fun onRegistrar() {
        val estado = _uiState.value

        val errorRazonSocial = validarRazonSocial(estado.razonSocial)
        val errorRut = validarRut(estado.rut)
        val errorGiro = validarGiro(estado.giro)
        val errorContacto = validarContacto(estado.contacto)
        val errorTelefono = validarTelefono(estado.telefono)
        val errorCorreo = validarCorreo(estado.correo)
        val errorNombreInstalacion = validarNombreInstalacion(estado.nombreInstalacion)
        val errorUbicacion = validarUbicacion(estado.ubicacion)
        val errorComuna = validarComuna(estado.comuna)
        val errorRegion = validarRegion(estado.region)

        val hayErrores = listOf(
            errorRazonSocial,
            errorRut,
            errorGiro,
            errorContacto,
            errorTelefono,
            errorCorreo,
            errorNombreInstalacion,
            errorUbicacion,
            errorComuna,
            errorRegion
        ).any { it != null }

        if (hayErrores) {
            _uiState.update {
                it.copy(
                    errorRazonSocial = errorRazonSocial,
                    errorRut = errorRut,
                    errorGiro = errorGiro,
                    errorContacto = errorContacto,
                    errorTelefono = errorTelefono,
                    errorCorreo = errorCorreo,
                    errorNombreInstalacion = errorNombreInstalacion,
                    errorUbicacion = errorUbicacion,
                    errorComuna = errorComuna,
                    errorRegion = errorRegion,
                    registroExitoso = false
                )
            }
            return
        }

        val rutFormateado = ValidacionUtils.formatearRut(estado.rut)

        viewModelScope.launch {
            val resultado = empresaRepository.registrarEmpresaConInstalacion(
                razonSocial = estado.razonSocial.trim(),
                rut = rutFormateado,
                giro = estado.giro.trim(),
                contacto = estado.contacto.trim(),
                telefono = estado.telefono.trim(),
                correo = estado.correo.trim().lowercase(),
                nombreInstalacion = estado.nombreInstalacion.trim(),
                ubicacion = estado.ubicacion.trim(),
                comuna = estado.comuna.trim(),
                region = estado.region.trim(),
                observaciones = estado.observacionesAcceso.trim()
            )

            resultado.onSuccess { idGenerado ->
                _uiState.update {
                    it.copy(
                        rut = rutFormateado,
                        idEmpresaGenerado = idGenerado,
                        registroExitoso = true,
                        errorGlobal = null
                    )
                }
            }.onFailure { excepcion ->
                val mensajeError = excepcion.message ?: "Error desconocido"

                if (mensajeError.contains("RUT")) {
                    _uiState.update { it.copy(errorRut = mensajeError, registroExitoso = false) }
                } else {
                    _uiState.update { it.copy(errorGlobal = mensajeError, registroExitoso = false) }
                }
            }
        }
    }
}