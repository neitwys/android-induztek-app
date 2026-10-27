package com.induztek.app.data

import android.content.Context
import androidx.core.content.edit

class SesionPreferencias(context: Context) {


    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    companion object {
        private const val CLAVE_ID_USUARIO = "id_usuario"
        private const val CLAVE_MODO_TEMA = "pref_modo_tema" // Clave para guardar el tema
        private const val SIN_SESION = -1L
    }


    fun guardar(idUsuario: Long) {
        prefs.edit { putLong(CLAVE_ID_USUARIO, idUsuario) }
    }


    fun leer(): Long = prefs.getLong(CLAVE_ID_USUARIO, SIN_SESION)

    // Borra el ID guardado (se usa cuando el usuario presiona "Cerrar Sesión")
    fun borrar() {
        prefs.edit { remove(CLAVE_ID_USUARIO) }
    }


    fun guardarModoTema(modo: String) {
        prefs.edit { putString(CLAVE_MODO_TEMA, modo) }
    }


    fun obtenerModoTema(): String {
        return prefs.getString(CLAVE_MODO_TEMA, "SISTEMA") ?: "SISTEMA"
    }
}