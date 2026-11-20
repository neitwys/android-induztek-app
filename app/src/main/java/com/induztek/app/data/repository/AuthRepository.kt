package com.induztek.app.data.repository

import com.induztek.app.model.Usuario

interface AuthRepository {
    suspend fun autenticar(correo: String, contrasenaPlana: String): Usuario
}