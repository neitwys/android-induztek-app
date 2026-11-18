package com.induztek.app.utils

object ValidacionUtils {

    /**
     * Valida un RUT chileno usando el algoritmo oficial Módulo 11.
     * Acepta formatos con o sin puntos y guion (ej: "76.123.456-K" o "76123456k").
     */
    fun esRutValido(rutInput: String): Boolean {
        val rutLimpio = rutInput.replace(".", "").replace("-", "").trim().uppercase()
        if (rutLimpio.length < 8 || rutLimpio.length > 9) return false

        val cuerpo = rutLimpio.dropLast(1)
        val dvIngresado = rutLimpio.last()

        if (!cuerpo.all { it.isDigit() }) return false
        if (!dvIngresado.isDigit() && dvIngresado != 'K') return false

        var suma = 0
        var multiplicador = 2

        for (i in cuerpo.length - 1 downTo 0) {
            suma += cuerpo[i].digitToInt() * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }

        val resto = 11 - (suma % 11)
        val dvEsperado = when (resto) {
            11 -> '0'
            10 -> 'K'
            else -> resto.digitToChar()
        }

        return dvIngresado == dvEsperado
    }

    /**
     * Formatea un RUT limpio a XX.XXX.XXX-X para mostrarlo ordenado en pantalla o BD.
     */
    fun formatearRut(rutInput: String): String {
        val limpio = rutInput.replace(".", "").replace("-", "").trim().uppercase()
        if (limpio.length < 2) return limpio

        val cuerpo = limpio.dropLast(1)
        val dv = limpio.last()

        val cuerpoFormateado = cuerpo.reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()

        return "$cuerpoFormateado-$dv"
    }

    /**
     * Valida teléfonos chilenos:
     * - 9 dígitos comenzando con 9 (ej: 912345678)
     * - Con prefijo +569 o 569 (ej: +56912345678 / +56 9 1234 5678)
     * - Teléfonos fijos de 9 dígitos comenzando con 2 (ej: 221234567)
     */
    fun esTelefonoChilenoValido(telefonoInput: String): Boolean {
        val limpio = telefonoInput.replace(" ", "").replace("-", "").replace("+", "")
        return when {
            limpio.length == 9 && (limpio.startsWith("9") || limpio.startsWith("2")) ->
                limpio.all { it.isDigit() }
            limpio.length == 11 && (limpio.startsWith("569") || limpio.startsWith("562")) ->
                limpio.all { it.isDigit() }
            else -> false
        }
    }

    /**
     * Valida que una medición sea un número real válido dentro de un rango físico lógico.
     */
    fun validarMedicionNumerica(
        valorTexto: String,
        minimo: Double = 0.0,
        maximo: Double = 10000.0,
        unidad: String = ""
    ): String? {
        val limpio = valorTexto.trim().replace(",", ".")
        if (limpio.isBlank()) return "La medición es obligatoria"

        val numero = limpio.toDoubleOrNull()
            ?: return "Ingrese un valor numérico válido (ej: 45.5)"

        if (numero < minimo || numero > maximo) {
            val sufijo = if (unidad.isNotBlank()) " $unidad" else ""
            return "El valor debe estar entre $minimo y $maximo$sufijo"
        }

        return null
    }
}