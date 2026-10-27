package com.induztek.app.utils

object PreguntasSeguridadUtils {
    val preguntas = mapOf(
        1 to "¿Cuál es el nombre de tu primera mascota?",
        2 to "¿Cuál es tu banda favorita?",
        3 to "¿Cuál es tu color favorito?"
    )

    //Esto es para mostrarlo en el MenuBox al momento de seleccionar la pregunta de seguridad
    val listaTextos: List<String> get() = preguntas.values.toList()

    //Se obtiene el ID de la pregunta de seguridad a partir del texto seleccionado en el MenuBox
    fun obtenerIdPorTexto(texto: String): Int {
        return preguntas.entries.firstOrNull { it.value == texto }?.key ?: 1
    }

    // Obtiene el texto a partir del ID de la base de datos (para mostrarlo cuando recupere contraseña)
    fun obtenerTextoPorId(id: Int): String {
        return preguntas[id] ?: preguntas[1]!! // Devuelve la primera por defecto si hay error
    }
}