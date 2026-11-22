package com.induztek.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.app.R
import com.induztek.app.ui.components.TarjetaSeccion
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.viewmodel.MiEmpresaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiEmpresaScreen(
    empresaId: Long,
    viewModel: MiEmpresaViewModel = viewModel(),
    onVolver: () -> Unit
) {
    val empresa = viewModel.empresaSeleccionada

    LaunchedEffect(empresaId) {
        if (empresaId > 0L) {
            viewModel.cargarDatosDeMiEmpresa(empresaId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Empresa") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            painter = painterResource(R.drawable.ic_volver),
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.espacioGrande),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            TarjetaSeccion(titulo = "Información de la Organización") {
                ItemInfoEmpresa(
                    etiqueta = "Razón Social / Nombre",
                    valor = empresa?.razonSocial ?: "",
                    iconoRes = R.drawable.ic_instalacion
                )

                ItemInfoEmpresa(
                    etiqueta = "RUT",
                    valor = empresa?.rut ?: "",
                    iconoRes = R.drawable.ic_buscar
                )

                ItemInfoEmpresa(
                    etiqueta = "Giro",
                    valor = empresa?.giro ?: "No especificado",
                    iconoRes = R.drawable.ic_buscar
                )
            }

            if (empresa?.contactos != null && empresa.contactos.isNotEmpty()) {
                TarjetaSeccion(titulo = "Contactos") {
                    empresa.contactos.forEachIndexed { index, contacto ->
                        Column {
                            ItemInfoEmpresa(
                                etiqueta = contacto.cargo ?: "Contacto",
                                valor = contacto.nombre,
                                iconoRes = R.drawable.ic_person
                            )

                            Column(modifier = Modifier.padding(start = 40.dp, top = Dimens.espacioChico)) {
                                if (!contacto.telefono.isNullOrBlank()) {
                                    Text(
                                        text = contacto.telefono,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (!contacto.correo.isNullOrBlank()) {
                                    Text(
                                        text = contacto.correo,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        if (index < empresa.contactos.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = Dimens.espacioChico),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }

            if (empresa?.instalaciones != null && empresa.instalaciones.isNotEmpty()) {
                TarjetaSeccion(titulo = "Instalaciones / Ubicaciones") {
                    empresa.instalaciones.forEachIndexed { index, instalacion ->
                        Column {
                            ItemInfoEmpresa(
                                etiqueta = "Instalación",
                                valor = instalacion.nombreInstalacion,
                                iconoRes = R.drawable.ic_instalacion
                            )

                            val direccionCompleta = listOfNotNull(
                                instalacion.ubicacion?.takeIf { it.isNotBlank() },
                                instalacion.comuna?.takeIf { it.isNotBlank() },
                                instalacion.region?.takeIf { it.isNotBlank() }
                            ).joinToString(", ")

                            if (direccionCompleta.isNotBlank()) {
                                Text(
                                    text = direccionCompleta,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 40.dp, top = Dimens.espacioChico)
                                )
                            }
                        }

                        if (index < empresa.instalaciones.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = Dimens.espacioChico),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemInfoEmpresa(
    etiqueta: String,
    valor: String,
    iconoRes: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconoRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(Dimens.espacioMedio))
        Column {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = valor.ifBlank { "..." },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}