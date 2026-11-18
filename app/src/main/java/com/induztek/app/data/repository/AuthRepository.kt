package com.induztek.app.data.repository

import android.content.Context
import com.induztek.app.data.InduztekDbHelper
import com.induztek.app.model.Usuario
import com.induztek.app.utils.SeguridadUtils

interface AuthRepository {
    fun autenticar(correo: String, contrasenaPlana: String): Usuario?
}

class LocalAuthRepository(context: Context) : AuthRepository {
    private val dbHelper = InduztekDbHelper(context.applicationContext)

    override fun autenticar(correo: String, contrasenaPlana: String): Usuario? {
        val correoLimpio = correo.lowercase().trim()
        val usuario = dbHelper.buscarPorCorreo(correoLimpio) ?: return null
        val hashIngresado = SeguridadUtils.hashSHA256(contrasenaPlana.trim())
        return if (hashIngresado == usuario.contrasena) usuario else null
    }
}