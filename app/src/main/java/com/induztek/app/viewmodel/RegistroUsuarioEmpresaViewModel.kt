package com.induztek.app.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.induztek.app.data.InduztekDbHelper
import com.induztek.app.model.RegistroUsuarioEmpresaUiState
import com.induztek.app.utils.SeguridadUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistroUsuarioEmpresaViewModel(application: Application): AndroidViewModel(application) {
    private val dbHelper = InduztekDbHelper(application)
    private val _uiState = MutableStateFlow(RegistroUsuarioEmpresaUiState())
    val uiState: StateFlow<RegistroUsuarioEmpresaUiState> = _uiState.asStateFlow()

    //Eventos de los campos
        //Este para pasar el id de la empresa que se registró en la pantalla anterior, para que el representante quede asociado a esa empresa.
    fun inicializarEmpresaId(empresaId: Long) {
        _uiState.update { it.copy(empresaId = empresaId) }
    }

    //Eventos del Representate de la Empresa

    fun onNombreCambia(valor: String) {
        _uiState.value = _uiState.value.copy(nombre = valor, errorNombre = null, registroExitoso = false)
    }

    fun onCorreoCambia(valor: String) {
        _uiState.value = _uiState.value.copy(correo = valor, errorCorreo = null, registroExitoso = false)
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.value = _uiState.value.copy(contrasena = valor, errorContrasena = null, registroExitoso = false)
    }

    fun onTipoPreguntaClaveCambia(valor: Int) {
        _uiState.value = _uiState.value.copy(tipoPreguntaClave = valor, errorRespuestaPregunta = null, registroExitoso = false)
    }

    fun onRespuestaPreguntaClaveCambia(valor: String) {
        _uiState.value = _uiState.value.copy(respuestaPreguntaClave = valor, errorRespuestaPregunta = null, registroExitoso = false)
    }

    fun onAlternarVisibilidadContrasena() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    // Validaciones para botón registrarse

    private fun validarNombre(nombre: String): String? {
        return if (nombre.isBlank()) "El nombre es obligatorio" else null
    }

    private fun validarCorreo(correo: String): String? {
        if (correo.isBlank()) return null
        val esValido = Patterns.EMAIL_ADDRESS.matcher(correo).matches()
        return if (!esValido) "Ingrese un correo válido (ej: usuario@dominio.cl)" else null
    }

    private fun validarContrasena(contrasena: String): String? {
        return if (contrasena.isBlank()) "La contraseña es obligatoria" else null
    }

    private fun validarRespuestaPregunta(respuesta: String): String? {
        return if (respuesta.isBlank()) "La respuesta es obligatoria" else null
    }

    fun onRegistrar() {
        val estado = _uiState.value

        val errorNombre = validarNombre(estado.nombre)
        val errorCorreo = validarCorreo(estado.correo)
        val errorContrasena = validarContrasena(estado.contrasena)
        val errorRespuestaPregunta = validarRespuestaPregunta(estado.respuestaPreguntaClave)

        val hayErrores = listOf(
            errorNombre,
            errorCorreo,
            errorContrasena,
            errorRespuestaPregunta
        ).any { it != null }

        if (hayErrores) {
            _uiState.update { it.copy(
                errorNombre = errorNombre,
                errorCorreo = errorCorreo,
                errorContrasena = errorContrasena,
                errorRespuestaPregunta = errorRespuestaPregunta,
                registroExitoso = false
            ) }
            return
        }

        val contrasenaHasheada = SeguridadUtils.hashSHA256(estado.contrasena)

        // Si no hay errores, se puede registrar
        val exitoDb = dbHelper.insertarUsuario(
            nombre = estado.nombre,
            correo = estado.correo,
            contrasena = contrasenaHasheada,
            tipoPregunta = estado.tipoPreguntaClave,
            respuestaPregunta = estado.respuestaPreguntaClave,
            rolId = 4, //Representante de la empresa
            empresaId = estado.empresaId
        )

        if (exitoDb) {
            _uiState.update { it.copy(registroExitoso = true) }
        } else {
            _uiState.update {
                it.copy(
                    errorCorreo = "El correo ya se encuentra registrado",
                    registroExitoso = false
                )
            }
        }
    }
}