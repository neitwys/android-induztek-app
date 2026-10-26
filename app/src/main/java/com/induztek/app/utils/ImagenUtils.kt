package com.induztek.app.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImagenUtils {

    /**
     * Copia la imagen desde la Uri temporal/galería al almacenamiento privado de la app.
     * Retorna la ruta absoluta del archivo guardado (ej: /data/user/0/com.induztek.app/files/perfil_12345.jpg).
     */
    fun guardarImagenEnAlmacenamientoInterno(
        context: Context,
        uriOrigen: Uri,
        prefijoNombre: String
    ): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uriOrigen)
            val nombreArchivo = "${prefijoNombre}_${System.currentTimeMillis()}.jpg"
            val archivoDestino = File(context.filesDir, nombreArchivo)
            val outputStream = FileOutputStream(archivoDestino)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            // Retorna la ruta absoluta persistente
            archivoDestino.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}