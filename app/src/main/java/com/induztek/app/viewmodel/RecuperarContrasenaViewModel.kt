package com.induztek.app.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.data.repository.RemoteUsuarioRepository
import com.induztek.app.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecuperarContrasenaUiState(
    val correo: String = "",
    val codigoEnviado: Boolean = false,
    val codigo: String = "",
    val nuevaContrasena: String = "",
    val cargando: Boolean = false,
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)

class RecuperarContrasenaViewModel(
    application: Application,
    private val usuarioRepository: UsuarioRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        usuarioRepository = RemoteUsuarioRepository()
    )

    private val _uiState = MutableStateFlow(RecuperarContrasenaUiState())
    val uiState: StateFlow<RecuperarContrasenaUiState> = _uiState.asStateFlow()

    fun onCorreoCambia(correo: String) {
        _uiState.update {
            it.copy(
                correo = correo,
                codigoEnviado = false,
                codigo = "",
                nuevaContrasena = "",
                mensajeError = null,
                mensajeExito = null
            )
        }
    }

    fun onCodigoCambia(codigo: String) {
        if (codigo.length <= 6 && codigo.all { it.isDigit() }) {
            _uiState.update { it.copy(codigo = codigo, mensajeError = null, mensajeExito = null) }
        }
    }

    fun onNuevaContrasenaCambia(clave: String) {
        _uiState.update { it.copy(nuevaContrasena = clave, mensajeError = null, mensajeExito = null) }
    }

    fun enviarCodigo() {
        val correo = _uiState.value.correo.trim()
        if (correo.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _uiState.update { it.copy(mensajeError = "Ingrese un correo electrónico válido.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null, mensajeExito = null) }
            try {
                usuarioRepository.solicitarCodigoRecuperacion(correo)
                _uiState.update {
                    it.copy(
                        cargando = false,
                        codigoEnviado = true,
                        mensajeExito = "Enviamos un código de 6 dígitos a tu correo. Revisa tu bandeja (válido por 10 minutos)."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(cargando = false, mensajeError = e.message ?: "No se pudo enviar el código.")
                }
            }
        }
    }

    fun restablecerContrasena() {
        val estado = _uiState.value
        if (estado.codigo.length != 6) {
            _uiState.update { it.copy(mensajeError = "Ingrese el código de 6 dígitos enviado a su correo.") }
            return
        }
        if (estado.nuevaContrasena.trim().length < 8) {
            _uiState.update { it.copy(mensajeError = "La nueva contraseña debe tener al menos 8 caracteres.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null, mensajeExito = null) }
            try {
                usuarioRepository.recuperarContrasena(
                    correo = estado.correo.trim(),
                    codigo = estado.codigo,
                    nuevaContrasenaPlana = estado.nuevaContrasena
                )
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeExito = "Contraseña actualizada con éxito. Ya puede iniciar sesión.",
                        correo = "",
                        codigoEnviado = false,
                        codigo = "",
                        nuevaContrasena = ""
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(cargando = false, mensajeError = e.message ?: "Error al actualizar la contraseña.")
                }
            }
        }
    }
}