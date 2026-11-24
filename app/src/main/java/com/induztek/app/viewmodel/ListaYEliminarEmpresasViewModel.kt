package com.induztek.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.data.EmpresaConInstalacion
import com.induztek.app.data.repository.EmpresaRepository
import com.induztek.app.data.repository.RemoteEmpresaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListaYEliminarEmpresasViewModel(
    application: Application,
    private val empresaRepository: EmpresaRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        empresaRepository = RemoteEmpresaRepository()
    )

    private var todasLasEmpresas: List<EmpresaConInstalacion> = emptyList()

    private val _filtro = MutableStateFlow("")
    val filtro: StateFlow<String> = _filtro.asStateFlow()

    private val _empresas = MutableStateFlow<List<EmpresaConInstalacion>>(emptyList())
    val empresas: StateFlow<List<EmpresaConInstalacion>> = _empresas.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        recargar()
    }

    fun recargar() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            todasLasEmpresas = empresaRepository.listarEmpresas()
            aplicarFiltro()
            _cargando.value = false
        }
    }

    private fun aplicarFiltro() {
        val busqueda = _filtro.value.trim().lowercase()
        _empresas.value = if (busqueda.isEmpty()) {
            todasLasEmpresas
        } else {
            todasLasEmpresas.filter {
                it.razonSocial.lowercase().contains(busqueda) ||
                        it.rut.lowercase().contains(busqueda) ||
                        it.nombreInstalacion.lowercase().contains(busqueda) ||
                        it.comuna.lowercase().contains(busqueda)
            }
        }
    }

    fun buscar(texto: String) {
        _filtro.value = texto
        aplicarFiltro()
    }

    fun eliminar(empresa: EmpresaConInstalacion) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            val eliminado = empresaRepository.eliminarEmpresa(empresa.id)
            if (eliminado) {
                todasLasEmpresas = todasLasEmpresas.filterNot { it.id == empresa.id }
                aplicarFiltro()
            } else {
                _error.value = "No se pudo eliminar la empresa"
            }

            _cargando.value = false
        }
    }
}