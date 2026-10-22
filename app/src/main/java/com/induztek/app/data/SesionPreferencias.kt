package com.induztek.app.data

import android.content.Context
import androidx.core.content.edit

class SesionPreferencias(context: Context) {

    // Abre el archivo "sesion.xml" de forma privada (solo accesible por la app)
    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    companion object {
        private const val CLAVE_ID_USUARIO = "id_usuario"
        private const val SIN_SESION = -1L
    }

    // Guarda el ID del usuario cuando hace un Login exitoso
    fun guardar(idUsuario: Long) {
        prefs.edit { putLong(CLAVE_ID_USUARIO, idUsuario) }
    }

    // Lee el ID guardado. Si devuelve -1L, significa que nadie ha iniciado sesión.
    fun leer(): Long = prefs.getLong(CLAVE_ID_USUARIO, SIN_SESION)

    // Borra el ID guardado (se usa cuando el usuario presiona "Cerrar Sesión")
    fun borrar() {
        prefs.edit { remove(CLAVE_ID_USUARIO) }
    }
}