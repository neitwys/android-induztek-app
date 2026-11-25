package com.induztek.app.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CrearContrasenaViewModelTest {
    private lateinit var viewModel: CrearContrasenaViewModel

    @Before
    fun preparar() {
        viewModel = CrearContrasenaViewModel()
    }

    @Test
    fun onGuardarContrasena_camposVacios_muestraErrores() {
        viewModel.onGuardarContrasena(1L) { }

        assertEquals("La contraseña es obligatoria", viewModel.uiState.value.errorContrasena)
        assertEquals("Debes confirmar tu contraseña", viewModel.uiState.value.errorConfirmar)
    }

    @Test
    fun onGuardarContrasena_contrasenaCorta_muestraErrorLongitud() {
        viewModel.onContrasenaCambia("1234")

        viewModel.onGuardarContrasena(1L) { }

        assertEquals("La contraseña debe tener mínimo 8 caracteres", viewModel.uiState.value.errorContrasena)
    }

    @Test
    fun onGuardarContrasena_contrasenasNoCoinciden_muestraErrorConfirmar() {
        viewModel.onContrasenaCambia("clave1234")
        viewModel.onConfirmarContrasenaCambia("otra1234")

        viewModel.onGuardarContrasena(1L) { }

        assertNull(viewModel.uiState.value.errorContrasena)
        assertEquals("Las contraseñas no coinciden", viewModel.uiState.value.errorConfirmar)
    }

    @Test
    fun onContrasenaCambia_despuesError_limpiaErrorContrasena() {
        viewModel.onGuardarContrasena(1L) { }

        viewModel.onContrasenaCambia("clave1234")

        assertNull(viewModel.uiState.value.errorContrasena)
        assertEquals("Debes confirmar tu contraseña", viewModel.uiState.value.errorConfirmar)
    }

    @Test
    fun onConfirmarContrasenaCambia_despuesError_limpiaErrorConfirmar() {
        viewModel.onGuardarContrasena(1L) { }

        viewModel.onConfirmarContrasenaCambia("clave1234")

        assertNull(viewModel.uiState.value.errorConfirmar)
    }

    @Test
    fun onAlternarVisibilidadContrasena_cambiaEstadoVisible() {
        assertFalse(viewModel.uiState.value.contrasenaVisible)

        viewModel.onAlternarVisibilidadContrasena()

        assertTrue(viewModel.uiState.value.contrasenaVisible)
    }
}