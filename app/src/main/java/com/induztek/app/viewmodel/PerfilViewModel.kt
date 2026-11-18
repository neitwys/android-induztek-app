package com.induztek.app.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel

import com.induztek.app.data.repository.LocalUsuarioRepository
import com.induztek.app.data.repository.UsuarioRepository
import com.induztek.app.model.Usuario
import com.induztek.app.utils.ImagenUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

data class PerfilUiState(
    val usuario: Usuario? = null,
    val fotoUri: Uri? = null,
    // Formulario Editar Perfil
    val editandoPerfil: Boolean = false,
    val nombreEditado: String = "",
    val errorNombre: String? = null,
    val correoEditado: String = "",
    val errorCorreo: String? = null,
    // Formulario Cambiar Contraseña (con doble verificación)
    val cambiandoClave: Boolean = false,
    val claveActual: String = "",
    val errorClaveActual: String? = null,
    val textoPreguntaSeguridad: String = "Pregunta de seguridad",
    val respuestaSeguridad: String = "",
    val errorRespuestaSeguridad: String? = null,
    val claveNueva: String = "",
    val errorClaveNueva: String? = null,
    val confirmarClaveNueva: String = "",
    val errorConfirmarClave: String? = null,
    val clavesVisibles: Boolean = false,
    // Mensajes
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)

class PerfilViewModel(
    application: Application,
    private val usuarioRepository: UsuarioRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        usuarioRepository = LocalUsuarioRepository(application)
    )

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    fun cargarDatosUsuario(usuario: Usuario?) {
        if (usuario == null) return

        val usuarioBD = usuarioRepository.buscarPorId(usuario.id) ?: usuario
        val fotoUriCargada = usuarioBD.fotoPerfilPath?.let { path ->
            Uri.fromFile(File(path))
        }

        _uiState.update {
            it.copy(
                usuario = usuarioBD,
                fotoUri = fotoUriCargada,
                nombreEditado = usuarioBD.nombre,
                correoEditado = usuarioBD.correo,
                textoPreguntaSeguridad = obtenerTextoPregunta(usuarioBD.tipoPreguntaClave)
            )
        }
    }

    private fun obtenerTextoPregunta(tipoPregunta: Int): String = when (tipoPregunta) {
        1 -> "¿Cuál es el nombre de tu primera mascota?"
        2 -> "¿Cuál es tu banda o artista favorito?"
        3 -> "¿Cuál es tu color favorito?"
        else -> "Respuesta de seguridad registrada"
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

        val actualizado = usuarioRepository.actualizarDatosPerfil(
            idUsuario = usuarioActual.id,
            nombre = estado.nombreEditado,
            correo = estado.correoEditado
        )

        if (actualizado) {
            val usuarioRecargado = usuarioRepository.buscarPorId(usuarioActual.id) ?: usuarioActual.copy(
                nombre = estado.nombreEditado.trim(),
                correo = estado.correoEditado.trim().lowercase()
            )
            _uiState.update {
                it.copy(
                    usuario = usuarioRecargado,
                    editandoPerfil = false,
                    mensajeExito = "Perfil actualizado correctamente",
                    mensajeError = null
                )
            }
        } else {
            _uiState.update {
                it.copy(errorCorreo = "Este correo ya está en uso por otra cuenta")
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
                respuestaSeguridad = "",
                claveNueva = "",
                confirmarClaveNueva = "",
                errorClaveActual = null,
                errorRespuestaSeguridad = null,
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

    fun onRespuestaSeguridadCambia(valor: String) {
        _uiState.update { it.copy(respuestaSeguridad = valor, errorRespuestaSeguridad = null) }
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
        val errorRespuesta = if (estado.respuestaSeguridad.isBlank()) "Responda su pregunta de seguridad" else null
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

        if (errorActual != null || errorRespuesta != null || errorNueva != null || errorConfirmar != null) {
            _uiState.update {
                it.copy(
                    errorClaveActual = errorActual,
                    errorRespuestaSeguridad = errorRespuesta,
                    errorClaveNueva = errorNueva,
                    errorConfirmarClave = errorConfirmar
                )
            }
            return
        }

        val resultado = usuarioRepository.cambiarContrasena(
            idUsuario = usuarioActual.id,
            contrasenaActualPlana = estado.claveActual,
            respuestaSeguridad = estado.respuestaSeguridad,
            nuevaContrasenaPlana = estado.claveNueva
        )

        resultado.fold(
            onSuccess = {
                val usuarioRecargado = usuarioRepository.buscarPorId(usuarioActual.id) ?: usuarioActual
                _uiState.update {
                    it.copy(
                        usuario = usuarioRecargado,
                        cambiandoClave = false,
                        claveActual = "",
                        respuestaSeguridad = "",
                        claveNueva = "",
                        confirmarClaveNueva = "",
                        mensajeExito = "Contraseña actualizada con éxito",
                        mensajeError = null
                    )
                }
            },
            onFailure = { err ->
                val mensaje = err.message ?: "Error al validar credenciales"
                if (mensaje.contains("seguridad", ignoreCase = true) || mensaje.contains("respuesta", ignoreCase = true)) {
                    _uiState.update { it.copy(errorRespuestaSeguridad = mensaje) }
                } else {
                    _uiState.update { it.copy(errorClaveActual = mensaje) }
                }
            }

        )
    }

    // --- FOTO PERFIL ---
    fun actualizarFotoPerfil(uriOrigen: Uri?) {
        if (uriOrigen == null) return

        val context = getApplication<Application>().applicationContext
        val rutaPermanente = ImagenUtils.guardarImagenEnAlmacenamientoInterno(
            context = context,
            uriOrigen = uriOrigen,
            prefijoNombre = "perfil_usuario"
        )

        if (rutaPermanente != null) {
            val usuarioActual = _uiState.value.usuario
            if (usuarioActual != null) {
                usuarioRepository.actualizarFotoPerfil(usuarioActual.id, rutaPermanente)
            }
            val uriArchivoGuardado = Uri.fromFile(File(rutaPermanente))
            _uiState.update {
                it.copy(
                    fotoUri = uriArchivoGuardado,
                    mensajeExito = "Foto de perfil actualizada correctamente"
                )
            }
        } else {
            _uiState.update {
                it.copy(mensajeError = "No se pudo guardar la imagen localmente")
            }
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeExito = null, mensajeError = null) }
    }
}