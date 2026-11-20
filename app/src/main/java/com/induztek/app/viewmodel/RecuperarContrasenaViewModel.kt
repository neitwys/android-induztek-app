package com.induztek.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.data.repository.RemoteUsuarioRepository
import com.induztek.app.data.repository.UsuarioRepository
import com.induztek.app.utils.PreguntasSeguridadUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecuperarContrasenaUiState(
    val correo: String = "",
    val preguntaSeguridad: String? = null,
    val respuestaPregunta: String = "",
    val nuevaContrasena: String = "",
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
                preguntaSeguridad = null,
                respuestaPregunta = "",
                nuevaContrasena = "",
                mensajeError = null,
                mensajeExito = null
            )
        }
    }

    fun onRespuestaPreguntaCambia(respuesta: String) {
        _uiState.update { it.copy(respuestaPregunta = respuesta, mensajeError = null, mensajeExito = null) }
    }

    fun onNuevaContrasenaCambia(clave: String) {
        _uiState.update { it.copy(nuevaContrasena = clave, mensajeError = null, mensajeExito = null) }
    }

    fun buscarPreguntaSeguridad() {
        val estado = _uiState.value

        if (estado.correo.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Ingrese su correo electrónico.") }
            return
        }

        viewModelScope.launch {
            try {
                val tipoPregunta = usuarioRepository.obtenerPreguntaSeguridad(estado.correo.trim())
                _uiState.update {
                    it.copy(
                        preguntaSeguridad = PreguntasSeguridadUtils.obtenerTextoPorId(tipoPregunta),
                        respuestaPregunta = "",
                        nuevaContrasena = "",
                        mensajeError = null,
                        mensajeExito = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(mensajeError = e.message ?: "El correo ingresado no se encuentra registrado.")
                }
            }
        }
    }

    fun restablecerContrasena() {
        val estado = _uiState.value

        if (estado.respuestaPregunta.isBlank() || estado.nuevaContrasena.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Por favor complete todos los campos.") }
            return
        }

        viewModelScope.launch {
            try {
                usuarioRepository.recuperarContrasena(
                    correo = estado.correo.trim(),
                    respuestaSeguridad = estado.respuestaPregunta,
                    nuevaContrasenaPlana = estado.nuevaContrasena
                )
                _uiState.update {
                    it.copy(
                        mensajeExito = "Contraseña actualizada con éxito. Ya puede iniciar sesión con su nueva clave.",
                        mensajeError = null,
                        correo = "",
                        preguntaSeguridad = null,
                        respuestaPregunta = "",
                        nuevaContrasena = ""
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(mensajeError = e.message ?: "Error al actualizar la contraseña.")
                }
            }
        }
    }
}