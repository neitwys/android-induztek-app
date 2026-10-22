package com.induztek.app.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.induztek.app.data.UsuarioDbHelper
import com.induztek.app.model.LoginUiState
import com.induztek.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

//Cambiamos a AndroidViewModel, porque los DB y los Preferences (en data) necesitan de un Context.
//AndroidViewModel lo tiene, el ViewModel por defecto no.
class LoginViewModel(application: Application): AndroidViewModel(application) {
    private val dbHelper = UsuarioDbHelper(application)

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
            _uiState.update { it.copy(errorCorreo = "El correo no está registrado en el sistema") }
            return
        }

        if (estado.contrasena != usuarioEncontrado.contrasena) {
            _uiState.update { it.copy(errorContrasena = "Contraseña incorrecta") }
            return
        }

        onExito(usuarioEncontrado)
    }
}