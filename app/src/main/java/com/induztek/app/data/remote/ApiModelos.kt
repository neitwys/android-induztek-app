package com.induztek.app.data.remote

import com.induztek.app.model.Rol
import com.induztek.app.model.Usuario

data class LoginRequest(
    val correo: String,
    val contrasena: String
)

data class RegistroUsuarioRequest(
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: Long,
    val fotoPerfil: String? = null,
    val empresaId: Long? = null
)

data class ActualizarPerfilRequest(
    val nombre: String,
    val correo: String
)

data class CambiarContrasenaRequest(
    val contrasenaActual: String,
    val nuevaContrasena: String
)

data class SolicitarCodigoRequest(
    val correo: String
)

data class RecuperarContrasenaRequest(
    val correo: String,
    val codigo: String,
    val nuevaContrasena: String
)

data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val correo: String,
    val rol: Long,
    val fotoPerfil: String?,
    val empresaId: Long?,
    val activo: Boolean? = true,
    val requiereCambioClave: Boolean? = false
)

fun UsuarioResponse.aUsuario(): Usuario {
    val nombreRol = when (rol) {
        Rol.ID_TECNICO -> "Técnico de Terreno"
        Rol.ID_INGENIERO -> "Ingeniero de Pruebas"
        Rol.ID_JEFE -> "Jefe Técnico"
        Rol.ID_REPRESENTANTE_EMPRESA -> "Representante de Empresa"
        else -> "Usuario"
    }

    val urlCompletaFoto = fotoPerfil?.let { ruta ->
        if (ruta.startsWith("http")) ruta
        else RetrofitClient.URL_BASE.trimEnd('/') + ruta
    }

    return Usuario(
        id = id,
        nombre = nombre,
        correo = correo,
        contrasena = "",
        rolId = rol,
        rolNombre = nombreRol,
        tipoPreguntaClave = 0,
        respuestaPreguntaClave = "",
        empresaId = empresaId,
        fotoPerfilPath = urlCompletaFoto,
        activo = activo ?: true,
        requiereCambioClave = requiereCambioClave ?: false
    )
}

data class CrearPrimeraContrasenaRequest(
    val nuevaContrasena: String
)