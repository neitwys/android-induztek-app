package com.induztek.app.model

import android.net.Uri

data class PerfilUiState(
    val usuario: Usuario? = null,
    val fotoUri: Uri? = null,
    val editandoPerfil: Boolean = false,
    val nombreEditado: String = "",
    val errorNombre: String? = null,
    val correoEditado: String = "",
    val errorCorreo: String? = null,
    val cambiandoClave: Boolean = false,
    val claveActual: String = "",
    val errorClaveActual: String? = null,
    val claveNueva: String = "",
    val errorClaveNueva: String? = null,
    val confirmarClaveNueva: String = "",
    val errorConfirmarClave: String? = null,
    val clavesVisibles: Boolean = false,
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)