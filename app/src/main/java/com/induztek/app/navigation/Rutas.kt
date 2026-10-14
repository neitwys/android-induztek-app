package com.induztek.app.navigation

sealed class Ruta(val ruta: String) {
    data object Login: Ruta("login")
    data object MenuPrincipal: Ruta("menu_principal")
    data object RegistroCliente: Ruta("registro_cliente")
}