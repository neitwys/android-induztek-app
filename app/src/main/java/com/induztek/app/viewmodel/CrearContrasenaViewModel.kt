package com.induztek.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.data.repository.RemoteUsuarioRepository
import com.induztek.app.data.repository.UsuarioRepository
import com.induztek.app.model.CrearContrasenaUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CrearContrasenaViewModel(
    private val usuarioRepository: UsuarioRepository = RemoteUsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CrearContrasenaUiState())
    val uiState: StateFlow<CrearContrasenaUiState> = _uiState.asStateFlow()

    fun onContrasenaCambia(valor: String) {
        _uiState.update { it.copy(nuevaContrasena = valor, errorContrasena = null, errorGeneral = null) }
    }

    fun onConfirmarContrasenaCambia(valor: String) {
        _uiState.update { it.copy(confirmarContrasena = valor, errorConfirmar = null, errorGeneral = null) }
    }

    fun onAlternarVisibilidadContrasena() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onGuardarContrasena(idUsuario: Long, onExito: () -> Unit) {
        val estado = _uiState.value

        val errorContrasena = when {
            estado.nuevaContrasena.isBlank() -> "La contraseña es obligatoria"
            estado.nuevaContrasena.length < 8 -> "La contraseña debe tener mínimo 8 caracteres"
            else -> null
        }

        val errorConfirmar = when {
            estado.confirmarContrasena.isBlank() -> "Debes confirmar tu contraseña"
            estado.nuevaContrasena != estado.confirmarContrasena -> "Las contraseñas no coinciden"
            else -> null
        }

        if (errorContrasena != null || errorConfirmar != null) {
            _uiState.update {
                it.copy(
                    errorContrasena = errorContrasena,
                    errorConfirmar = errorConfirmar,
                    errorGeneral = null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, errorGeneral = null) }
            try {
                usuarioRepository.crearPrimeraContrasena(idUsuario, estado.nuevaContrasena)
                _uiState.update { it.copy(cargando = false) }
                onExito()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        errorGeneral = e.message ?: "Error al guardar la contraseña."
                    )
                }
            }
        }
    }
}