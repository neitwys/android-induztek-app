package com.induztek.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.utils.PreguntasSeguridadUtils

@Composable
fun SeleccionPreguntaSeguridad(
    preguntaSeleccionadaId: Int,
    onPreguntaSeleccionada: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listaIdsPreguntas = PreguntasSeguridadUtils.preguntas.keys.toList()

    Column(modifier = modifier.padding(vertical = Dimens.espacioMedio)) {
        Text(
            text = "Selecciona una pregunta de seguridad",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(Dimens.espacioMedio))

        GrupoRadio(
            opciones = listaIdsPreguntas,
            seleccionada = preguntaSeleccionadaId,
            onSeleccionar = onPreguntaSeleccionada,
            textoDe = { id -> PreguntasSeguridadUtils.obtenerTextoPorId(id) }
        )
    }
}

@Composable
fun <T> GrupoRadio(
    opciones: List<T>,
    seleccionada: T,
    onSeleccionar: (T) -> Unit,
    textoDe: (T) -> String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.selectableGroup()) {
        opciones.forEach { opcion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = opcion == seleccionada,
                        onClick = { onSeleccionar(opcion) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = Dimens.espacioMini),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = opcion == seleccionada, onClick = null)
                Text(
                    text = textoDe(opcion),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = Dimens.espacioChico)
                )
            }
        }
    }
}