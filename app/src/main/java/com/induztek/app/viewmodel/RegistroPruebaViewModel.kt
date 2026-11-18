package com.induztek.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.induztek.app.data.repository.LocalPruebaRepository
import com.induztek.app.data.repository.PruebaRepository
import com.induztek.app.model.ProtocoloPrueba
import com.induztek.app.model.RegistroPruebaUiState
import com.induztek.app.utils.ImagenUtils
import com.induztek.app.utils.ValidacionUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroPruebaViewModel(
    application: Application,
    private val pruebaRepository: PruebaRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        pruebaRepository = LocalPruebaRepository(application)
    )

    private val _uiState = MutableStateFlow(RegistroPruebaUiState())
    val uiState: StateFlow<RegistroPruebaUiState> = _uiState.asStateFlow()

    init {
        cargarEquipos()
    }

    private fun cargarEquipos() {
        val equipos = pruebaRepository.listarEquipos()
        val primerId = equipos.firstOrNull()?.idEquipo ?: 1L
        _uiState.update { it.copy(listaEquipos = equipos, idEquipoSeleccionado = primerId) }
    }

    fun onEquipoSeleccionado(idEquipo: Long) {
        _uiState.update { it.copy(idEquipoSeleccionado = idEquipo) }
    }

    fun onTipoPruebaCambia(tipo: String) {
        val unidadInferida = inferirUnidadDesdeTipo(tipo)
        _uiState.update {
            it.copy(
                tipoPrueba = tipo,
                unidadMedida = unidadInferida,
                errorTipoPrueba = null,
                errorValorMedicion = null
            )
        }
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

    fun onFotoCapturada(uriString: String) {
        val context = getApplication<Application>().applicationContext
        val uriOrigen = Uri.parse(uriString)

        val rutaPermanente = ImagenUtils.guardarImagenEnAlmacenamientoInterno(
            context = context,
            uriOrigen = uriOrigen,
            prefijoNombre = "evidencia_prueba"
        )

        if (rutaPermanente != null) {
            _uiState.update { it.copy(fotoUri = rutaPermanente) }
        }
    }

    fun obtenerUbicacionGps() {
        val latSimulada = -33.4489
        val lonSimulada = -70.6693
        _uiState.update { it.copy(latitud = latSimulada, longitud = lonSimulada) }
    }

    fun onGuardarProtocolo(idUsuarioTecnico: Long) {
        val estado = _uiState.value

        val errorTipo = when {
            estado.tipoPrueba.isBlank() -> "El tipo de prueba es obligatorio"
            estado.tipoPrueba.trim().length < 3 -> "Ingrese un nombre de prueba descriptivo"
            else -> null
        }

        val (minimo, maximo, unidad) = obtenerRangoPermitido(estado.tipoPrueba)
        val errorMedicion = ValidacionUtils.validarMedicionNumerica(
            valorTexto = estado.valorMedicion,
            minimo = minimo,
            maximo = maximo,
            unidad = unidad
        )

        if (errorTipo != null || errorMedicion != null) {
            _uiState.update {
                it.copy(
                    errorTipoPrueba = errorTipo,
                    errorValorMedicion = errorMedicion
                )
            }
            return
        }

        val valorNormalizado = estado.valorMedicion.trim().replace(",", ".").toDouble()
        val medicionFormateada = "$valorNormalizado $unidad"

        val formatoFecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fechaActual = formatoFecha.format(Date())

        val nuevoProtocolo = ProtocoloPrueba(
            idEquipo = estado.idEquipoSeleccionado,
            idUsuarioTecnico = idUsuarioTecnico,
            fechaHora = fechaActual,
            tipoPrueba = estado.tipoPrueba.trim(),
            valorMedicion = medicionFormateada,
            resultado = estado.resultado,
            estadoProtocolo = "REGISTRADO_TERRENO",
            observaciones = estado.observaciones.trim(),
            fotoUri = estado.fotoUri,
            latitud = estado.latitud,
            longitud = estado.longitud
        )

        val exito = pruebaRepository.registrarProtocolo(nuevoProtocolo)

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
                errorTipoPrueba = null,
                errorValorMedicion = null,
                registroExitoso = false
            )
        }
    }

    private fun inferirUnidadDesdeTipo(tipoPrueba: String): String {
        val t = tipoPrueba.lowercase()
        return when {
            "aisl" in t || "megoh" in t -> "MΩ"
            "tens" in t || "volt" in t -> "V"
            "corr" in t || "amp" in t -> "A"
            "temp" in t || "term" in t -> "°C"
            "tiemp" in t || "dispar" in t -> "ms"
            else -> "MΩ"
        }
    }

    private fun obtenerRangoPermitido(tipoPrueba: String): Triple<Double, Double, String> {
        val t = tipoPrueba.lowercase()
        return when {
            "aisl" in t || "megoh" in t -> Triple(0.1, 5000.0, "MΩ")
            "tens" in t || "volt" in t -> Triple(0.0, 35000.0, "V")
            "corr" in t || "amp" in t -> Triple(0.0, 2000.0, "A")
            "temp" in t || "term" in t -> Triple(-20.0, 150.0, "°C")
            "tiemp" in t || "dispar" in t -> Triple(1.0, 5000.0, "ms")
            else -> Triple(0.0, 10000.0, "MΩ")
        }
    }
}