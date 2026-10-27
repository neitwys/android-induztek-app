package com.induztek.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.induztek.app.data.InduztekDbHelper
import com.induztek.app.utils.PreguntasSeguridadUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

data class RecuperarContrasenaUiState(
    val correo: String = "",
    val preguntaSeguridad: String? = null,
    val respuestaPregunta: String = "",
    val nuevaContrasena: String = "",
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)

class RecuperarContrasenaViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = InduztekDbHelper(application)

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

        val usuario = dbHelper.buscarPorCorreo(estado.correo.trim())
        if (usuario == null) {
            _uiState.update { it.copy(mensajeError = "El correo ingresado no se encuentra registrado.") }
            return
        }

        _uiState.update {
            it.copy(
                preguntaSeguridad = PreguntasSeguridadUtils.obtenerTextoPorId(usuario.tipoPreguntaClave),
                respuestaPregunta = "",
                nuevaContrasena = "",
                mensajeError = null,
                mensajeExito = null
            )
        }
    }

    fun restablecerContrasena() {
        val estado = _uiState.value

        if (estado.respuestaPregunta.isBlank() || estado.nuevaContrasena.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Por favor complete todos los campos.") }
            return
        }

        val usuario = dbHelper.buscarPorCorreo(estado.correo.trim())
        if (usuario == null) {
            _uiState.update { it.copy(mensajeError = "El correo ingresado no se encuentra registrado.") }
            return
        }

        val respuestaIngresada = estado.respuestaPregunta.trim().lowercase(Locale.ROOT)
        val respuestaRegistrada = usuario.respuestaPreguntaClave.trim().lowercase(Locale.ROOT)
        if (respuestaIngresada != respuestaRegistrada) {
            _uiState.update { it.copy(mensajeError = "Respuesta incorrecta.") }
            return
        }

        val filasAfectadas = dbHelper.actualizarContrasena(usuario.id, estado.nuevaContrasena)
        if (filasAfectadas > 0) {
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
        } else {
            _uiState.update { it.copy(mensajeError = "Error al actualizar la contraseña.") }
        }
    }
}