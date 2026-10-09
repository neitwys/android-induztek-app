package com.induztek.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.induztek.app.R
import com.induztek.app.ui.styles.EstilosCampo
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.ui.theme.InduztekAppTheme

@Composable
fun CampoTexto(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    @DrawableRes icono: Int,
    error: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        label = { Text(etiqueta) },
        modifier = modifier,

        leadingIcon = {
            Icon(
                painter = painterResource(id = icono),
                contentDescription = null,
                modifier = Modifier.size(Dimens.tamanoIcono)
            )
        },
        isError = error != null,
        keyboardOptions = KeyboardOptions(
            keyboardType = tipoTeclado,
            imeAction = ImeAction.Next
        ),

        supportingText = { if (error != null) Text(error)},
        singleLine = true,
        shape = EstilosCampo.forma,
        colors = EstilosCampo.colores()
    )
}

@Preview(showBackground = true)
@Composable
private fun CampoTextoPreview() {
    InduztekAppTheme {
        Column(
            modifier = Modifier.padding(Dimens.espacioMedio)
        ) {
            CampoTexto(
                valor = "Jose Cas342",
                onValorCambia = {},
                etiqueta = "Nombre completo",
                icono = R.drawable.ic_person,
                error = "Ingresa un nombre válido",
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.padding(Dimens.espacioMini))

            CampoTexto(
                valor = "Jose Castro",
                onValorCambia = {},
                etiqueta = "Nombre completo",
                icono = R.drawable.ic_person,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

}