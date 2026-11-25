package com.induztek.app.viewmodel

import android.app.Application
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class RegistroEmpresaViewModelTest {
    private lateinit var viewModel: RegistroEmpresaViewModel

    @Before
    fun preparar() {
        viewModel = RegistroEmpresaViewModel(Application())
    }

    @Test
    fun onRegistrar_camposVacios_muestraErroresObligatorios() {
        viewModel.onRegistrar()

        val estado = viewModel.uiState.value
        assertEquals("La razón social es obligatoria", estado.errorRazonSocial)
        assertEquals("El RUT es obligatorio", estado.errorRut)
        assertEquals("El giro comercial es obligatorio", estado.errorGiro)
        assertEquals("El nombre de contacto es obligatorio", estado.errorContacto)
        assertEquals("El teléfono es obligatorio", estado.errorTelefono)
        assertEquals("El correo corporativo es obligatorio", estado.errorCorreo)
        assertEquals("El nombre de la instalación es obligatorio", estado.errorNombreInstalacion)
        assertEquals("La dirección es obligatoria", estado.errorUbicacion)
        assertEquals("La comuna es obligatoria", estado.errorComuna)
        assertEquals("La región es obligatoria", estado.errorRegion)
        assertFalse(estado.registroExitoso)
    }

    @Test
    fun onRegistrar_datosCortosOInvalidos_muestraErroresDeFormato() {
        viewModel.onRazonSocialCambia("AB")
        viewModel.onRutCambia("12345")
        viewModel.onGiroCambia("ABC")
        viewModel.onContactoCambia("Jo")
        viewModel.onTelefonoCambia("123")
        viewModel.onNombreInstalacionCambia("P1")
        viewModel.onUbicacionCambia("Av 1")

        viewModel.onRegistrar()

        val estado = viewModel.uiState.value
        assertEquals("Debe tener al menos 3 caracteres", estado.errorRazonSocial)
        assertEquals("RUT inválido (ej: 76.543.210-9)", estado.errorRut)
        assertEquals("Ingrese un giro descriptivo", estado.errorGiro)
        assertEquals("Ingrese nombre y apellido", estado.errorContacto)
        assertEquals("Formato inválido (ej: +56912345678 o 912345678)", estado.errorTelefono)
        assertEquals("Debe tener al menos 3 caracteres", estado.errorNombreInstalacion)
        assertEquals("Ingrese calle y numeración o referencia", estado.errorUbicacion)
    }

    @Test
    fun onCamposCambian_despuesError_limpianErroresIndividuales() {
        viewModel.onRegistrar()

        viewModel.onRazonSocialCambia("Minera Andina SpA")
        viewModel.onRutCambia("76.123.456-7")
        viewModel.onGiroCambia("Servicios Industriales")
        viewModel.onContactoCambia("Carlos Pérez")
        viewModel.onTelefonoCambia("+56912345678")
        viewModel.onCorreoCambia("contacto@andina.ejemplo")
        viewModel.onNombreInstalacionCambia("Planta Norte")
        viewModel.onUbicacionCambia("Av. Industrial 1234")
        viewModel.onRegionCambia("Antofagasta")
        viewModel.onComunaCambia("Antofagasta")

        val estado = viewModel.uiState.value
        assertNull(estado.errorRazonSocial)
        assertNull(estado.errorRut)
        assertNull(estado.errorGiro)
        assertNull(estado.errorContacto)
        assertNull(estado.errorTelefono)
        assertNull(estado.errorCorreo)
        assertNull(estado.errorNombreInstalacion)
        assertNull(estado.errorUbicacion)
        assertNull(estado.errorComuna)
        assertNull(estado.errorRegion)
    }

    @Test
    fun onLimpiar_restableceTodosLosCamposYErrores() {
        viewModel.onRazonSocialCambia("Minera Andina SpA")
        viewModel.onObservacionesAccesoCambia("Portón norte")
        viewModel.onRegistrar()

        viewModel.onLimpiar()

        val estado = viewModel.uiState.value
        assertEquals("", estado.razonSocial)
        assertEquals("", estado.observacionesAcceso)
        assertNull(estado.errorRut)
        assertNull(estado.errorComuna)
        assertFalse(estado.registroExitoso)
    }
}