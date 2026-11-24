package com.induztek.app.viewmodel

import android.app.Application
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

class LoginViewModelTest {
    private lateinit var viewModel: LoginViewModel

    @Before
    fun preparar() {
        viewModel = LoginViewModel(Application())
    }

    @Test
    fun onIniciarSesion_camposVacios_muestraErrores() {
        viewModel.onIniciarSesion {  }

        assertEquals("El correo es obligatorio", viewModel.uiState.value.errorCorreo)
        assertEquals("La contraseña es obligatoria", viewModel.uiState.value.errorContrasena)
    }

    @Test
    fun onIniciarSesion_contrasenaCorta_muestraErrorLongitud() {
        viewModel.onContrasenaCambia("1234")

        viewModel.onIniciarSesion {  }

        assertEquals("La contraseña debe tener mínimo 8 caracteres", viewModel.uiState.value.errorContrasena)
    }

    @Test
    fun onCorreoCambia_despuesError_limpiaErrorCorreo() {
        viewModel.onIniciarSesion {  }

        viewModel.onCorreoCambia("mgodoy@induztek.ejemplo")

        assertNull(viewModel.uiState.value.errorCorreo)
        assertEquals("La contraseña es obligatoria", viewModel.uiState.value.errorContrasena)
    }

    @Test
    fun onAlternarVisibilidadContrasena_poneEstadoVisible() {
        assertFalse(viewModel.uiState.value.contrasenaVisible)

        viewModel.onAlternarVisibilidadContrasena()

        assertTrue(viewModel.uiState.value.contrasenaVisible)
    }
}