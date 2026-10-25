package com.induztek.app.viewmodel

import android.app.Application
import android.annotation.SuppressLint
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationServices
import com.induztek.app.data.InduztekDbHelper
import com.induztek.app.model.ProtocoloPrueba
import com.induztek.app.model.RegistroPruebaUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroPruebaViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = InduztekDbHelper(application)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    private val _uiState = MutableStateFlow(RegistroPruebaUiState())
    val uiState: StateFlow<RegistroPruebaUiState> = _uiState.asStateFlow()

    init {
        cargarEquipos()
    }

    fun cargarEquipos() {
        val equipos = dbHelper.listarEquipos()
        _uiState.update {
            it.copy(
                listaEquipos = equipos,
                idEquipoSeleccionado = equipos.firstOrNull()?.idEquipo ?: 0L
            )
        }
    }

    fun onEquipoSeleccionado(idEquipo: Long) {
        _uiState.update { it.copy(idEquipoSeleccionado = idEquipo) }
    }

    fun onTipoPruebaCambia(tipo: String) {
        _uiState.update { it.copy(tipoPrueba = tipo) }
    }

    fun onValorMedicionCambia(valor: String) {
        _uiState.update { it.copy(valorMedicion = valor, errorValorMedicion = null, registroExitoso = false) }
    }

    fun onResultadoCambia(resultado: String) {
        _uiState.update { it.copy(resultado = resultado) }
    }

    fun onObservacionesCambia(obs: String) {
        _uiState.update { it.copy(observaciones = obs) }
    }

    fun onFotoCapturada(uriString: String?) {
        _uiState.update { it.copy(fotoUri = uriString) }
    }

    @SuppressLint("MissingPermission")
    fun obtenerUbicacionGps() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    _uiState.update {
                        it.copy(
                            latitud = location.latitude,
                            longitud = location.longitude
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onGuardarProtocolo(idUsuarioTecnico: Long) {
        val estado = _uiState.value

        if (estado.valorMedicion.isBlank()) {
            _uiState.update { it.copy(errorValorMedicion = "Ingrese el valor numérico o resultado de la prueba") }
            return
        }

        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        val nuevoProtocolo = ProtocoloPrueba(
            idEquipo = estado.idEquipoSeleccionado,
            idUsuarioTecnico = if (idUsuarioTecnico > 0) idUsuarioTecnico else 1L, // Técnico por defecto si no hay sesión
            fechaHora = fechaActual,
            tipoPrueba = estado.tipoPrueba,
            valorMedicion = estado.valorMedicion,
            resultado = estado.resultado,
            estadoProtocolo = "REGISTRADO_TERRENO", // Estado que exige el caso de estudio Induztek
            observaciones = estado.observaciones,
            fotoUri = estado.fotoUri,
            latitud = estado.latitud,
            longitud = estado.longitud
        )

        val exito = dbHelper.insertarProtocolo(nuevoProtocolo)
        _uiState.update { it.copy(registroExitoso = exito) }
    }

    fun limpiarFormulario() {
        _uiState.update {
            RegistroPruebaUiState(
                listaEquipos = it.listaEquipos,
                idEquipoSeleccionado = it.listaEquipos.firstOrNull()?.idEquipo ?: 0L
            )
        }
    }
}