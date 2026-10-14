package com.induztek.app.viewmodel

import androidx.lifecycle.ViewModel
import com.induztek.app.model.MenuPrincipalUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuPrincipalViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(MenuPrincipalUiState())
    val uiState: StateFlow<MenuPrincipalUiState> = _uiState.asStateFlow()
}