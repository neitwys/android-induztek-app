package com.induztek.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.induztek.app.data.repository.LocalUsuarioRepository
import com.induztek.app.data.repository.UsuarioRepository
import com.induztek.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ListaYEliminarUsuariosViewModel(
    application: Application,
    private val usuarioRepository: UsuarioRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        usuarioRepository = LocalUsuarioRepository(application)
    )

    private val _filtro = MutableStateFlow("")
    val filtro: StateFlow<String> = _filtro.asStateFlow()

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    init {
        recargar()
    }

    private fun recargar() {
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
    }

    fun buscar(texto: String) {
        _filtro.value = texto
        recargar()
    }

    fun eliminar(usuario: Usuario) {
        usuarioRepository.eliminarUsuario(usuario.id)
        recargar()
    }
}