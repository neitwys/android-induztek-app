package com.induztek.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.induztek.app.data.InduztekDbHelper
import com.induztek.app.model.Equipo
import com.induztek.app.model.ProtocoloPrueba
import com.induztek.app.model.RegistroPruebaUiState
import com.induztek.app.utils.ImagenUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroPruebaViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = InduztekDbHelper(application)

    private val _uiState = MutableStateFlow(RegistroPruebaUiState())
    val uiState: StateFlow<RegistroPruebaUiState> = _uiState.asStateFlow()

    init {
        cargarEquipos()
    }

    private fun cargarEquipos() {
        val equipos = dbHelper.listarEquipos()
        _uiState.update { it.copy(listaEquipos = equipos) }
    }

    fun onEquipoSeleccionado(idEquipo: Long) {
        _uiState.update { it.copy(idEquipoSeleccionado = idEquipo) }
    }

    fun onTipoPruebaCambia(tipo: String) {
        _uiState.update { it.copy(tipoPrueba = tipo) }
    }

    fun onValorMedicionCambia(valor: String) {
        _uiState.update { it.copy(valorMedicion = valor, errorValorMedicion = null) }
    }

    fun onResultadoCambia(resultado: String) {
        _uiState.update { it.copy(resultado = resultado) }
    }

    fun onObservacionesCambia(obs: String) {
        _uiState.update { it.copy(observaciones = obs) }
    }

    // --- MANEJO PERSISTENTE DE LA FOTO DE TERRENO ---
    fun onFotoCapturada(uriString: String) {
        val context = getApplication<Application>().applicationContext
        val uriOrigen = Uri.parse(uriString)

        // Copiamos la imagen al almacenamiento interno privado de la app
        val rutaPermanente = ImagenUtils.guardarImagenEnAlmacenamientoInterno(
            context = context,
            uriOrigen = uriOrigen,
            prefijoNombre = "evidencia_prueba"
        )

        if (rutaPermanente != null) {
            // Guardamos la ruta absoluta persistente (ej: /data/user/0/.../evidencia_prueba_123.jpg)
            _uiState.update { it.copy(fotoUri = rutaPermanente) }
        }
    }

    fun obtenerUbicacionGps() {
        // Simulación o captura real de coordenadas GPS
        val latSimulada = -33.4489
        val lonSimulada = -70.6693
        _uiState.update { it.copy(latitud = latSimulada, longitud = lonSimulada) }
    }

    fun onGuardarProtocolo(idUsuarioTecnico: Long) {
        val estado = _uiState.value

        if (estado.valorMedicion.isBlank()) {
            _uiState.update { it.copy(errorValorMedicion = "Ingrese un valor de medición válido") }
            return
        }

        val formatoFecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fechaActual = formatoFecha.format(Date())

        val nuevoProtocolo = ProtocoloPrueba(
            idEquipo = estado.idEquipoSeleccionado,
            idUsuarioTecnico = idUsuarioTecnico,
            fechaHora = fechaActual,
            tipoPrueba = estado.tipoPrueba,
            valorMedicion = estado.valorMedicion,
            resultado = estado.resultado,
            estadoProtocolo = "REGISTRADO_TERRENO",
            observaciones = estado.observaciones,
            fotoUri = estado.fotoUri, // Guarda la ruta absoluta local en SQLite
            latitud = estado.latitud,
            longitud = estado.longitud
        )

        val exito = dbHelper.insertarProtocolo(nuevoProtocolo)

        if (exito) {
            _uiState.update { it.copy(registroExitoso = true) }
        }
    }

    fun limpiarFormulario() {
        _uiState.update {
            it.copy(
                valorMedicion = "",
                observaciones = "",
                fotoUri = null,
                latitud = null,
                longitud = null,
                registroExitoso = false
            )
        }
    }
}