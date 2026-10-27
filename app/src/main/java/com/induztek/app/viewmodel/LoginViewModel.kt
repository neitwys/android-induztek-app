package com.induztek.app.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.induztek.app.data.InduztekDbHelper
import com.induztek.app.model.LoginUiState
import com.induztek.app.model.Usuario
import com.induztek.app.utils.SeguridadUtils // Importamos nuestra utilidad de encriptación
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(application: Application): AndroidViewModel(application) {
    private val dbHelper = InduztekDbHelper(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoCambia(correo: String) {
        _uiState.update { it.copy(correo = correo, errorCorreo = null, errorGeneral = null)}
    }

    fun onContrasenaCambia(contrasena: String) {
        _uiState.update { it.copy(contrasena = contrasena, errorContrasena = null, errorGeneral = null)}
    }

    fun onAlternarVisibilidadContrasena() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onIniciarSesion(onExito: (Usuario) -> Unit) {
        val estado = _uiState.value

        val errorCorreo = when {
            estado.correo.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(estado.correo).matches() -> "Ingrese un correo válido"
            else -> null
        }
        val errorContrasena = when {
            estado.contrasena.isBlank() -> "La contraseña es obligatoria"
            estado.contrasena.length < 8 -> "La contraseña debe tener mínimo 8 caracteres"
            else -> null
        }

        if (errorCorreo != null || errorContrasena != null) {
            _uiState.update {
                it.copy(
                    errorCorreo = errorCorreo,
                    errorContrasena = errorContrasena
                )
            }
            return
        }

        val correoLimpio = estado.correo.lowercase().trim()
        val usuarioEncontrado = dbHelper.buscarPorCorreo(correoLimpio)

        if (usuarioEncontrado == null) {
            _uiState.update { it.copy(errorCorreo = "El correo y/o contraseña están incorrectas.") }
            return
        }

        // Convertimos la clave ingresada a Hash SHA-256 antes de comparar
        val contrasenaHashIngresada = SeguridadUtils.hashSHA256(estado.contrasena.trim())

        if (contrasenaHashIngresada != usuarioEncontrado.contrasena) {
            _uiState.update { it.copy(errorContrasena = "El correo y/o contraseña están incorrectas.") }
            return
        }

        onExito(usuarioEncontrado)
    }
}