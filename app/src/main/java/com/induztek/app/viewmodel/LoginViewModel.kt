package com.induztek.app.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.induztek.app.model.LoginUiState
import com.induztek.app.model.RolUsuario
import com.induztek.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoCambia(correo: String) {
        _uiState.update { it.copy(correo = correo, errorCorreo = null, errorGeneral = null)}
    }
    fun onContrasenaCambia(contrasena: String) {
        _uiState.update { it.copy(contrasena = contrasena, errorContrasena = null, errorGeneral = null)}
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

        //Esto es por mientras, todas las contrasenas son 12345678.
        if (estado.contrasena != "12345678") {
            _uiState.update { it.copy(errorContrasena = "Contraseña incorrecta") }
            return
        }

        //Esto es por mientras, de ahí con Base de Datos haremos los usuarios.
        val usuarioAutenticado = when (estado.correo.lowercase().trim()) {
            "mgodoy@induztek.ejemplo" ->
                Usuario(
                    "1",
                    "Matías Godoy Herrera",
                    "mgodoy@induztek.ejemplo",
                    RolUsuario.TECNICO)

            "acontreras@induztek.ejemplo" ->
                Usuario(
                    "2",
                    "Andrea Contreras Silva",
                    "acontreras@induztek.ejemplo",
                    RolUsuario.INGENIERO
                )

            "respinoza@induztek.ejemplo" ->
                Usuario(
                    "3",
                    "Ricardo Espinoza Lagos",
                    "respinoza@induztek.ejemplo",
                    RolUsuario.JEFE_TECNICO
                )

            "mfuentes@envpacifico.ejemplo" ->
                Usuario(
                    "4",
                    "Marcela Fuentes Rojas",
                    "mfuentes@envpacifico.ejemplo",
                    RolUsuario.CLIENTE,
                    "Envasadora del Pacífico"
                )

            else -> {
                _uiState.update { it.copy(errorCorreo = "El correo no está registrado en el sistema") }
                return
            }
        }
        onExito(usuarioAutenticado)
    }

}