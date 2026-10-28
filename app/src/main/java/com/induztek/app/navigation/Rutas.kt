package com.induztek.app.navigation

sealed class Ruta(val ruta: String) {
    data object Login : Ruta("login")

    data object RecuperarContrasena : Ruta("recuperar_contrasena")
    data object MenuPrincipal : Ruta("menu_principal")

    data object Perfil : Ruta("perfil")
    data object RegistroEmpresa : Ruta("registro_empresa")
    data object RegistroUsuarioEmpresa : Ruta("registro_usuario_empresa/{empresaId}") {
        fun crearRuta(empresaId: Long): String = "registro_usuario_empresa/$empresaId"
    }
    data object Clientes : Ruta("clientes")
    object RegistroPrueba : Ruta("registro_prueba")
    object HistorialEquipos : Ruta("historial_equipos")
}