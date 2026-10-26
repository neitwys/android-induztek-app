package com.induztek.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.induztek.app.data.InduztekDbHelper
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
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = InduztekDbHelper(application)

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    fun cargarDatosUsuario(usuario: Usuario?) {
        if (usuario == null) return

        // Cargar los datos más actualizados desde la base de datos SQLite
        val usuarioBD = dbHelper.buscarPorId(usuario.id) ?: usuario

        // Si el usuario tiene una ruta de foto guardada en BD, la convertimos a Uri
        val fotoUriCargada = usuarioBD.fotoPerfilPath?.let { path ->
            Uri.fromFile(File(path))
        }

        _uiState.update {
            it.copy(
                usuario = usuarioBD,
                fotoUri = fotoUriCargada
            )
        }
    }

    fun actualizarFotoPerfil(uriOrigen: Uri?) {
        if (uriOrigen == null) return

        val context = getApplication<Application>().applicationContext

        // 1. Guardar la copia física permanente en context.filesDir
        val rutaPermanente = ImagenUtils.guardarImagenEnAlmacenamientoInterno(
            context = context,
            uriOrigen = uriOrigen,
            prefijoNombre = "perfil_usuario"
        )

        if (rutaPermanente != null) {
            val usuarioActual = _uiState.value.usuario

            if (usuarioActual != null) {
                // 2. Actualizar en la base de datos SQLite
                dbHelper.actualizarFotoPerfilUsuario(usuarioActual.id, rutaPermanente)
            }

            // 3. Crear Uri desde el archivo guardado y actualizar UI
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