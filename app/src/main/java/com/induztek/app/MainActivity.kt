package com.induztek.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.induztek.app.data.SesionPreferencias
import com.induztek.app.navigation.AppNavegacion
import com.induztek.app.ui.theme.InduztekAppTheme
import com.induztek.app.ui.theme.ModoTema

class MainActivity : ComponentActivity() {

    private lateinit var sesionPreferencias: SesionPreferencias

    // Estado reactivo para que Compose actualice el tema al instante
    private var modoTemaActual by mutableStateOf(ModoTema.SISTEMA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sesionPreferencias = SesionPreferencias(this)

        // Carga el tema guardado en preferencias al abrir la app
        val temaGuardado = sesionPreferencias.obtenerModoTema()
        modoTemaActual = try {
            ModoTema.valueOf(temaGuardado)
        } catch (e: Exception) {
            ModoTema.SISTEMA
        }

        setContent {
            InduztekAppTheme(modoTema = modoTemaActual) {
                AppNavegacion(
                    modoTemaActual = modoTemaActual,
                    onCambiarTema = { nuevoModo ->
                        modoTemaActual = nuevoModo
                        sesionPreferencias.guardarModoTema(nuevoModo.name)
                    }
                )
            }
        }
    }
}