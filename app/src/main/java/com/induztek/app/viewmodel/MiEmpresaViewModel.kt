package com.induztek.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.data.remote.EmpresaResponse
import com.induztek.app.data.repository.EmpresaRepository
import com.induztek.app.data.repository.RemoteEmpresaRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MiEmpresaViewModel(
    private val repository: EmpresaRepository = RemoteEmpresaRepository()
) : ViewModel() {

    var empresaSeleccionada by mutableStateOf<EmpresaResponse?>(null)
        private set

    fun cargarDatosDeMiEmpresa(empresaId: Long) {
        viewModelScope.launch {
            val resultado = repository.obtenerEmpresaPorId(empresaId)
            resultado.onSuccess { empresa ->
                empresaSeleccionada = empresa
            }
        }
    }
}