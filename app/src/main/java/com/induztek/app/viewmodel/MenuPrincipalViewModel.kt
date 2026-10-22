package com.induztek.app.viewmodel

import androidx.lifecycle.ViewModel
import com.induztek.app.model.MenuPrincipalUiState
import com.induztek.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MenuPrincipalViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(MenuPrincipalUiState())
    val uiState: StateFlow<MenuPrincipalUiState> = _uiState.asStateFlow()

    // Carga los datos del usuario autenticado en el estado de la UI
    fun cargarUsuario(usuario: Usuario?) {
        usuario?.let { u ->
            _uiState.update {
                it.copy(nombreUsuario = u.nombre)
            }
        }
    }
}