package com.induztek.app.model

data class Rol (
    val id: Long,
    val nombre: String
) {
    companion object {
        //ID de los roles.
        const val ID_TECNICO = 1L
        const val ID_INGENIERO = 2L
        const val ID_JEFE = 3L
        const val ID_CLIENTE = 4L

        //Rol por defecto, puede ser al registrarse.
        const val ID_DEFAULT = ID_TECNICO
    }
}