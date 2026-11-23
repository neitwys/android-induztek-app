package com.induztek.app.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.data.repository.RemoteUsuarioRepository
import com.induztek.app.data.repository.UsuarioRepository
import com.induztek.app.model.RegistroUsuarioEmpresaUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroUsuarioEmpresaViewModel(
    application: Application,
    private val usuarioRepository: UsuarioRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        usuarioRepository = RemoteUsuarioRepository()
    )

    private val _uiState = MutableStateFlow(RegistroUsuarioEmpresaUiState())
    val uiState: StateFlow<RegistroUsuarioEmpresaUiState> = _uiState.asStateFlow()

    fun inicializarEmpresaId(empresaId: Long) {
        val rolPorDefecto = if (empresaId > 0L) 4 else 1
        _uiState.update { it.copy(empresaId = empresaId, rolIdSeleccionado = rolPorDefecto) }
    }

    fun onRolCambia(nuevoRolId: Int) {
        _uiState.update { it.copy(rolIdSeleccionado = nuevoRolId, registroExitoso = false) }
    }

    fun onNombreCambia(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = null, registroExitoso = false) }
    }

    fun onCorreoCambia(valor: String) {
        _uiState.update { it.copy(correo = valor, errorCorreo = null, registroExitoso = false) }
    }

    private fun validarNombre(nombre: String): String? = when {
        nombre.isBlank() -> "El nombre completo es obligatorio"
        nombre.trim().length < 3 -> "Ingrese nombre y apellido (mínimo 3 caracteres)"
        else -> null
    }

    private fun validarCorreo(correo: String): String? = when {
        correo.isBlank() -> "El correo electrónico es obligatorio"
        !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() -> "Ingrese un correo válido (ej: usuario@induztek.cl)"
        else -> null
    }

    fun onRegistrar() {
        val estado = _uiState.value

        val errorNombre = validarNombre(estado.nombre)
        val errorCorreo = validarCorreo(estado.correo)

        if (errorNombre != null || errorCorreo != null) {
            _uiState.update {
                it.copy(
                    errorNombre = errorNombre,
                    errorCorreo = errorCorreo,
                    registroExitoso = false
                )
            }
            return
        }

        val empresaFk = if (estado.empresaId > 0L) estado.empresaId else null
        val rolFinal = if (estado.empresaId > 0L) 4L else estado.rolIdSeleccionado.toLong()
        val claveInicialActivacion = ""

        viewModelScope.launch {
            try {
                usuarioRepository.registrarUsuario(
                    nombre = estado.nombre.trim(),
                    correo = estado.correo.trim().lowercase(),
                    contrasenaPlana = claveInicialActivacion,
                    rolId = rolFinal,
                    empresaId = empresaFk
                )
                _uiState.update { it.copy(registroExitoso = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorCorreo = e.message ?: "No se pudo registrar el usuario",
                        registroExitoso = false
                    )
                }
            }
        }
    }
}