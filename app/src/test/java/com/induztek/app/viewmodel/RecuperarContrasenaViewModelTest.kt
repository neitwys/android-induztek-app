package com.induztek.app.viewmodel

import android.app.Application
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class RecuperarContrasenaViewModelTest {
    private lateinit var viewModel: RecuperarContrasenaViewModel

    @Before
    fun preparar() {
        viewModel = RecuperarContrasenaViewModel(Application())
    }

    @Test
    fun enviarCodigo_correoVacio_muestraMensajeError() {
        viewModel.enviarCodigo()

        assertEquals(
            "Ingrese un correo electrónico válido.", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun onCodigoCambia_soloAceptaHasta6DigitosNumericos() {
        viewModel.onCodigoCambia("123456")
        assertEquals("123456", viewModel.uiState.value.codigo)

        viewModel.onCodigoCambia("1234567")
        viewModel.onCodigoCambia("12ab")
        assertEquals("123456", viewModel.uiState.value.codigo)
    }

    @Test
    fun restablecerContrasena_codigoIncompleto_muestraError() {
        viewModel.onCodigoCambia("123")

        viewModel.restablecerContrasena()

        assertEquals(
            "Ingrese el código de 6 dígitos enviado a su correo.", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun restablecerContrasena_contrasenaCorta_muestraErrorLongitud() {
        viewModel.onCodigoCambia("123456")
        viewModel.onNuevaContrasenaCambia("1234")

        viewModel.restablecerContrasena()

        assertEquals(
            "La nueva contraseña debe tener al menos 8 caracteres.", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun onCorreoCambia_despuesError_limpiaErrorYReiniciaCodigo() {
        viewModel.onCodigoCambia("123456")
        viewModel.enviarCodigo()

        viewModel.onCorreoCambia("mgodoy@induztek.ejemplo")

        assertEquals("mgodoy@induztek.ejemplo", viewModel.uiState.value.correo)
        assertEquals("", viewModel.uiState.value.codigo)
        assertNull(viewModel.uiState.value.mensajeError)
    }

    @Test
    fun onNuevaContrasenaCambia_despuesError_limpiaMensajeError() {
        viewModel.onCodigoCambia("123456")
        viewModel.restablecerContrasena()

        viewModel.onNuevaContrasenaCambia("clave1234")

        assertNull(viewModel.uiState.value.mensajeError)
    }
}