package com.induztek.app.navigation

sealed class Ruta(val ruta: String) {
    data object Login : Ruta("login")

    data object RecuperarContrasena : Ruta("recuperar_contrasena")
    data object MenuPrincipal : Ruta("menu_principal")

    data object Perfil : Ruta("perfil")
    data object RegistroCliente : Ruta("registro_cliente")
    data object Clientes : Ruta("clientes")
    object RegistroPrueba : Ruta("registro_prueba")
    object HistorialEquipos : Ruta("historial_equipos")
}