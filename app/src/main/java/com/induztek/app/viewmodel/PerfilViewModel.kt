package com.induztek.app.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.data.repository.AvatarRepository
import com.induztek.app.data.repository.EmpresaRepository
import com.induztek.app.data.repository.RemoteEmpresaRepository
import com.induztek.app.data.repository.RemoteUsuarioRepository
import com.induztek.app.data.repository.UsuarioRepository
import com.induztek.app.model.PerfilUiState
import com.induztek.app.model.Usuario
import com.induztek.app.utils.ConvertidorImagenUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel(
    application: Application,
    private val usuarioRepository: UsuarioRepository,
    private val avatarRepository: AvatarRepository,
    private val empresaRepository: EmpresaRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        usuarioRepository = RemoteUsuarioRepository(),
        avatarRepository = AvatarRepository(),
        empresaRepository = RemoteEmpresaRepository()
    )

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    fun cargarDatosUsuario(usuario: Usuario?) {
        if (usuario == null) return

        aplicarUsuarioEnEstado(usuario)

        val idDeLaEmpresa = usuario.empresaId

        if (idDeLaEmpresa != null) {
            viewModelScope.launch {
                val resultado = empresaRepository.obtenerEmpresaPorId(idDeLaEmpresa)
                resultado.onSuccess { empresa ->
                    _uiState.update { it.copy(nombreEmpresa = empresa.razonSocial) }
                }.onFailure {
                    _uiState.update { it.copy(nombreEmpresa = "Empresa no encontrada") }
                }
            }
        }
        viewModelScope.launch {
            try {
                val usuarioServidor = usuarioRepository.buscarPorId(usuario.id)
                aplicarUsuarioEnEstado(usuarioServidor)
            } catch (_: Exception) {
            }
        }
    }

    private fun aplicarUsuarioEnEstado(usuario: Usuario) {
        val fotoUriCargada = usuario.fotoPerfilPath?.let { url -> Uri.parse(url) }
        _uiState.update {
            it.copy(
                usuario = usuario,
                fotoUri = fotoUriCargada,
                nombreEditado = usuario.nombre,
                correoEditado = usuario.correo
            )
        }
    }

    // --- EDITAR PERFIL ---
    fun onAlternarEditarPerfil() {
        val actual = _uiState.value.usuario
        _uiState.update {
            it.copy(
                editandoPerfil = !it.editandoPerfil,
                cambiandoClave = false,
                nombreEditado = actual?.nombre ?: "",
                correoEditado = actual?.correo ?: "",
                errorNombre = null,
                errorCorreo = null,
                mensajeExito = null,
                mensajeError = null
            )
        }
    }

    fun onNombreEditadoCambia(valor: String) {
        _uiState.update { it.copy(nombreEditado = valor, errorNombre = null) }
    }

    fun onCorreoEditadoCambia(valor: String) {
        _uiState.update { it.copy(correoEditado = valor, errorCorreo = null) }
    }

    fun onGuardarPerfil() {
        val estado = _uiState.value
        val usuarioActual = estado.usuario ?: return

        val errorNombre = when {
            estado.nombreEditado.isBlank() -> "El nombre es obligatorio"
            estado.nombreEditado.trim().length < 3 -> "Mínimo 3 caracteres"
            else -> null
        }

        val errorCorreo = when {
            estado.correoEditado.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(estado.correoEditado.trim()).matches() -> "Correo electrónico inválido"
            else -> null
        }

        if (errorNombre != null || errorCorreo != null) {
            _uiState.update { it.copy(errorNombre = errorNombre, errorCorreo = errorCorreo) }
            return
        }

        viewModelScope.launch {
            try {
                val usuarioActualizado = usuarioRepository.actualizarDatosPerfil(
                    idUsuario = usuarioActual.id,
                    nombre = estado.nombreEditado,
                    correo = estado.correoEditado
                )
                _uiState.update {
                    it.copy(
                        usuario = usuarioActualizado,
                        editandoPerfil = false,
                        mensajeExito = "Perfil actualizado correctamente",
                        mensajeError = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorCorreo = e.message ?: "Este correo ya está en uso por otra cuenta")
                }
            }
        }
    }

    // --- CAMBIAR CONTRASEÑA ---
    fun onAlternarCambiarClave() {
        _uiState.update {
            it.copy(
                cambiandoClave = !it.cambiandoClave,
                editandoPerfil = false,
                claveActual = "",
                claveNueva = "",
                confirmarClaveNueva = "",
                errorClaveActual = null,
                errorClaveNueva = null,
                errorConfirmarClave = null,
                mensajeExito = null,
                mensajeError = null
            )
        }
    }

    fun onClaveActualCambia(valor: String) {
        _uiState.update { it.copy(claveActual = valor, errorClaveActual = null) }
    }

    fun onClaveNuevaCambia(valor: String) {
        _uiState.update { it.copy(claveNueva = valor, errorClaveNueva = null) }
    }

    fun onConfirmarClaveNuevaCambia(valor: String) {
        _uiState.update { it.copy(confirmarClaveNueva = valor, errorConfirmarClave = null) }
    }

    fun onAlternarVisibilidadClaves() {
        _uiState.update { it.copy(clavesVisibles = !it.clavesVisibles) }
    }

    fun onGuardarNuevaClave() {
        val estado = _uiState.value
        val usuarioActual = estado.usuario ?: return

        val errorActual = if (estado.claveActual.isBlank()) "Ingrese su contraseña actual" else null
        val errorNueva = when {
            estado.claveNueva.isBlank() -> "Ingrese la nueva contraseña"
            estado.claveNueva.trim().length < 8 -> "Mínimo 8 caracteres"
            estado.claveNueva == estado.claveActual -> "La nueva clave debe ser distinta a la actual"
            else -> null
        }
        val errorConfirmar = when {
            estado.confirmarClaveNueva.isBlank() -> "Confirme la nueva contraseña"
            estado.confirmarClaveNueva != estado.claveNueva -> "Las contraseñas no coinciden"
            else -> null
        }

        if (errorActual != null || errorNueva != null || errorConfirmar != null) {
            _uiState.update {
                it.copy(
                    errorClaveActual = errorActual,
                    errorClaveNueva = errorNueva,
                    errorConfirmarClave = errorConfirmar
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                usuarioRepository.cambiarContrasena(
                    idUsuario = usuarioActual.id,
                    contrasenaActualPlana = estado.claveActual,
                    nuevaContrasenaPlana = estado.claveNueva
                )
                _uiState.update {
                    it.copy(
                        cambiandoClave = false,
                        claveActual = "",
                        claveNueva = "",
                        confirmarClaveNueva = "",
                        mensajeExito = "Contraseña actualizada con éxito",
                        mensajeError = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorClaveActual = e.message ?: "La contraseña actual es incorrecta")
                }
            }
        }
    }

    // --- FOTO PERFIL ---
    fun actualizarFotoPerfil(uriOrigen: Uri?) {
        if (uriOrigen == null) return
        val usuarioActual = _uiState.value.usuario ?: return
        val context = getApplication<Application>().applicationContext

        viewModelScope.launch {
            val bitmap = ConvertidorImagenUtils.desdeUri(context, uriOrigen)
            if (bitmap == null) {
                _uiState.update { it.copy(mensajeError = "No se pudo leer la imagen seleccionada") }
                return@launch
            }

            try {
                val bytes = ConvertidorImagenUtils.aBytes(bitmap)
                val usuarioActualizado = avatarRepository.subir(usuarioActual.id, bytes)
                val nuevaUri = usuarioActualizado.fotoPerfilPath?.let { Uri.parse(it) }
                _uiState.update {
                    it.copy(
                        usuario = usuarioActualizado,
                        fotoUri = nuevaUri,
                        mensajeExito = "Foto de perfil actualizada correctamente",
                        mensajeError = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(mensajeError = e.message ?: "No se pudo subir la foto al servidor")
                }
            }
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeExito = null, mensajeError = null) }
    }
}