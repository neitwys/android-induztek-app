package com.induztek.app.util

import java.security.MessageDigest

object SeguridadUtils {

    /**
     * Convierte un texto plano en su representación Hash SHA-256 (64 caracteres)
     */
    fun hashSHA256(texto: String): String {
        if (texto.isBlank()) return ""
        val bytes = MessageDigest.getInstance("SHA-256").digest(texto.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

