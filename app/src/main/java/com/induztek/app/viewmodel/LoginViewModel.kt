package com.induztek.app.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.induztek.app.data.repository.AuthRepository
import com.induztek.app.data.repository.LocalAuthRepository
import com.induztek.app.model.LoginUiState
import com.induztek.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    // Constructor secundario para que viewModel() siga funcionando sin cambios en LoginScreen
    constructor(application: Application) : this(
        application = application,
        authRepository = LocalAuthRepository(application)
    )

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoCambia(correo: String) {
        _uiState.update { it.copy(correo = correo, errorCorreo = null, errorGeneral = null) }
    }

    fun onContrasenaCambia(contrasena: String) {
        _uiState.update { it.copy(contrasena = contrasena, errorContrasena = null, errorGeneral = null) }
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
                    errorContrasena = errorContrasena,
                    errorGeneral = null
                )
            }
            return
        }

        val usuarioAutenticado = authRepository.autenticar(estado.correo, estado.contrasena)

        if (usuarioAutenticado == null) {
            _uiState.update {
                it.copy(
                    errorCorreo = null,
                    errorContrasena = null,
                    errorGeneral = "Correo o contraseña incorrectos."
                )
            }
            return
        }

        onExito(usuarioAutenticado)
    }
}