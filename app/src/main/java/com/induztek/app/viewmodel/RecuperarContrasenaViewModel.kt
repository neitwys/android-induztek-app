package com.induztek.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.induztek.app.data.InduztekDbHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RecuperarContrasenaUiState(
    val correo: String = "",
    val nuevaContrasena: String = "",
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)

class RecuperarContrasenaViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = InduztekDbHelper(application)

    private val _uiState = MutableStateFlow(RecuperarContrasenaUiState())
    val uiState: StateFlow<RecuperarContrasenaUiState> = _uiState.asStateFlow()

    fun onCorreoCambia(correo: String) {
        _uiState.update { it.copy(correo = correo, mensajeError = null, mensajeExito = null) }
    }

    fun onNuevaContrasenaCambia(clave: String) {
        _uiState.update { it.copy(nuevaContrasena = clave, mensajeError = null, mensajeExito = null) }
    }

    fun restablecerContrasena() {
        val estado = _uiState.value

        if (estado.correo.isBlank() || estado.nuevaContrasena.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Por favor complete todos los campos.") }
            return
        }

        val usuario = dbHelper.buscarPorCorreo(estado.correo.trim())
        if (usuario == null) {
            _uiState.update { it.copy(mensajeError = "El correo ingresado no se encuentra registrado.") }
            return
        }

        val filasAfectadas = dbHelper.actualizarContrasena(usuario.id, estado.nuevaContrasena)
        if (filasAfectadas > 0) {
            _uiState.update {
                it.copy(
                    mensajeExito = "Contraseña actualizada con éxito. Ya puede iniciar sesión con su nueva clave.",
                    mensajeError = null,
                    correo = "",
                    nuevaContrasena = ""
                )
            }
        } else {
            _uiState.update { it.copy(mensajeError = "Error al actualizar la contraseña.") }
        }
    }
}