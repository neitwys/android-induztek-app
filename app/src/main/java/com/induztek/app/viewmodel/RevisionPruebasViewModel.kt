package com.induztek.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.data.repository.PruebaRepository
import com.induztek.app.data.repository.RemotePruebaRepository
import com.induztek.app.model.ProtocoloPrueba
import com.induztek.app.model.RevisionPruebasUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RevisionPruebasViewModel(
    application: Application,
    private val pruebaRepository: PruebaRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        pruebaRepository = RemotePruebaRepository(application)
    )

    private val _uiState = MutableStateFlow(RevisionPruebasUiState())
    val uiState: StateFlow<RevisionPruebasUiState> = _uiState.asStateFlow()

    init {
        cargarPruebas()
    }

    fun cargarPruebas() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, errorMensaje = null) }
            val lista = withContext(Dispatchers.IO) {
                pruebaRepository.listarProtocolos()
            }
            _uiState.update {
                it.copy(
                    pruebas = lista,
                    cargando = false
                )
            }
        }
    }

    fun onCambiarFiltro(estado: String) {
        _uiState.update {
            it.copy(
                filtroEstado = estado,
                mensajeEstado = null,
                errorMensaje = null
            )
        }
    }

    fun onAbrirDetalle(prueba: ProtocoloPrueba) {
        _uiState.update {
            it.copy(
                pruebaSeleccionada = prueba,
                motivoRechazo = "",
                errorMensaje = null
            )
        }
    }

    fun onCerrarDetalle() {
        _uiState.update {
            it.copy(
                pruebaSeleccionada = null,
                motivoRechazo = "",
                errorMensaje = null
            )
        }
    }

    fun onMotivoRechazoCambia(motivo: String) {
        _uiState.update { it.copy(motivoRechazo = motivo, errorMensaje = null) }
    }

    fun aprobarPrueba(prueba: ProtocoloPrueba, idUsuarioIngeniero: Long) {
        viewModelScope.launch {
            val fechaRevision = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val actualizada = prueba.copy(
                resultado = "APROBADO",
                estadoProtocolo = "APROBADA",
                idUsuarioIngeniero = idUsuarioIngeniero,
                motivoRechazo = null,
                observaciones = construirObservacionRevision(
                    observacionOriginal = prueba.observaciones,
                    accion = "APROBADO por ingeniero #$idUsuarioIngeniero ($fechaRevision)",
                    motivo = null
                )
            )

            val ok = withContext(Dispatchers.IO) {
                pruebaRepository.actualizarProtocolo(actualizada)
            }

            if (ok) {
                val listaActualizada = withContext(Dispatchers.IO) {
                    pruebaRepository.listarProtocolos()
                }
                _uiState.update {
                    it.copy(
                        pruebas = listaActualizada,
                        pruebaSeleccionada = null,
                        motivoRechazo = "",
                        mensajeEstado = "✓ Prueba marcada como APROBADA"
                    )
                }
            } else {
                _uiState.update { it.copy(errorMensaje = "No se pudo aprobar la prueba") }
            }
        }
    }

    fun rechazarPrueba(prueba: ProtocoloPrueba, idUsuarioIngeniero: Long) {
        val motivo = _uiState.value.motivoRechazo.trim()
        if (motivo.isBlank()) {
            _uiState.update { it.copy(errorMensaje = "Indica un motivo para rechazar la prueba") }
            return
        }

        viewModelScope.launch {
            val fechaRevision = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val actualizada = prueba.copy(
                resultado = "RECHAZADO",
                estadoProtocolo = "RECHAZADA",
                idUsuarioIngeniero = idUsuarioIngeniero,
                motivoRechazo = motivo,
                observaciones = construirObservacionRevision(
                    observacionOriginal = prueba.observaciones,
                    accion = "RECHAZADO por ingeniero #$idUsuarioIngeniero ($fechaRevision)",
                    motivo = motivo
                )
            )

            val ok = withContext(Dispatchers.IO) {
                pruebaRepository.actualizarProtocolo(actualizada)
            }

            if (ok) {
                val listaActualizada = withContext(Dispatchers.IO) {
                    pruebaRepository.listarProtocolos()
                }
                _uiState.update {
                    it.copy(
                        pruebas = listaActualizada,
                        pruebaSeleccionada = null,
                        motivoRechazo = "",
                        mensajeEstado = "✓ Prueba marcada como RECHAZADA"
                    )
                }
            } else {
                _uiState.update { it.copy(errorMensaje = "No se pudo rechazar la prueba") }
            }
        }
    }

    private fun construirObservacionRevision(
        observacionOriginal: String,
        accion: String,
        motivo: String?
    ): String {
        val notaRevision = if (motivo.isNullOrBlank()) {
            "[$accion]"
        } else {
            "[$accion - Motivo: $motivo]"
        }
        return if (observacionOriginal.isBlank()) {
            notaRevision
        } else {
            "$observacionOriginal\n$notaRevision"
        }
    }
}