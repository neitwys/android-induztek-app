package com.induztek.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.data.repository.RemoteUsuarioRepository
import com.induztek.app.data.repository.UsuarioRepository
import com.induztek.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListaYEliminarUsuariosViewModel(
    application: Application,
    private val usuarioRepository: UsuarioRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        usuarioRepository = RemoteUsuarioRepository()
    )

    private val _filtro = MutableStateFlow("")
    val filtro: StateFlow<String> = _filtro.asStateFlow()

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    init {
        recargar()
    }

    private fun recargar() {
        viewModelScope.launch {
            try {
                val todas = usuarioRepository.listarUsuarios()
                val busqueda = _filtro.value.trim().lowercase()

                _usuarios.value = if (busqueda.isEmpty()) {
                    todas
                } else {
                    todas.filter {
                        it.nombre.lowercase().contains(busqueda) ||
                                it.correo.lowercase().contains(busqueda) ||
                                it.rolNombre.lowercase().contains(busqueda) ||
                                (it.empresaNombre?.lowercase()?.contains(busqueda) == true)
                    }
                }
            } catch (_: Exception) {
                _usuarios.value = emptyList()
            }
        }
    }

    fun buscar(texto: String) {
        _filtro.value = texto
        recargar()
    }

    fun eliminar(usuario: Usuario) {
        viewModelScope.launch {
            try {
                usuarioRepository.eliminarUsuario(usuario.id)
                recargar()
            } catch (_: Exception) {
            }
        }
    }
}