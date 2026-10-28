package com.induztek.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.induztek.app.data.EmpresaConInstalacion
import com.induztek.app.data.InduztekDbHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ListaYEliminarEmpresasViewModel(application: Application) : AndroidViewModel(application) {

    private val db = InduztekDbHelper(application)

    private val _filtro = MutableStateFlow("")
    val filtro: StateFlow<String> = _filtro.asStateFlow()

    private val _empresas = MutableStateFlow<List<EmpresaConInstalacion>>(emptyList())
    val empresas: StateFlow<List<EmpresaConInstalacion>> = _empresas.asStateFlow()

    init {
        recargar()
    }

    private fun recargar() {
        val todas = db.listarEmpresas()
        val busqueda = _filtro.value.trim().lowercase()

        _empresas.value = if (busqueda.isEmpty()) {
            todas
        } else {
            todas.filter {
                it.razonSocial.lowercase().contains(busqueda) ||
                        it.rut.lowercase().contains(busqueda) ||
                        it.nombreInstalacion.lowercase().contains(busqueda) ||
                        it.comuna.lowercase().contains(busqueda)
            }
        }
    }

    fun buscar(texto: String) {
        _filtro.value = texto
        recargar()
    }

    fun eliminar(empresa: EmpresaConInstalacion) {
        db.eliminarEmpresa(empresa.idEmpresa)
        recargar()
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}