package com.induztek.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.induztek.app.data.ClienteConInstalacion
import com.induztek.app.data.InduztekDbHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ClientesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = InduztekDbHelper(application)

    //Para buscar texto escrito en el buscador.
    private val _filtro = MutableStateFlow("")
    val filtro: StateFlow<String> = _filtro.asStateFlow()

    //La lista se carga, especificamente con los clientes aca.
    private val _clientes = MutableStateFlow(db.listarClientes())
    val clientes: StateFlow<List<ClienteConInstalacion>> = _clientes.asStateFlow()

    private fun recargar() {
        _clientes.value = db.listarClientes()
    }

    fun buscar(texto: String) {
        _filtro.value = texto
        recargar()
    }

    fun eliminar(cliente: ClienteConInstalacion) {
        db.eliminarCliente(cliente.idCliente)
        recargar()
    }

    //Al destruir el ViewModel se MATA y así no se queda detras.
    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}