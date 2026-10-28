package com.induztek.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.induztek.app.R
import com.induztek.app.data.EmpresaConInstalacion
import com.induztek.app.ui.components.CampoTexto
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.viewmodel.ListaYEliminarEmpresasViewModel

@Composable
fun ListaYEliminarEmpresasScreen(
    viewModel: ListaYEliminarEmpresasViewModel = viewModel(),
    onVolver: () -> Unit
) {
    val empresas by viewModel.empresas.collectAsStateWithLifecycle()
    val filtro by viewModel.filtro.collectAsStateWithLifecycle()

    ListaYEliminarEmpresasContent(
        empresas = empresas,
        onVolver = onVolver,
        filtro = filtro,
        onBuscar = viewModel::buscar,
        onEliminar = viewModel::eliminar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaYEliminarEmpresasContent(
    empresas: List<EmpresaConInstalacion>,
    onVolver: () -> Unit,
    filtro: String = "",
    onBuscar: (String) -> Unit = {},
    onEliminar: (EmpresaConInstalacion) -> Unit = {}
) {
    var porEliminar by remember { mutableStateOf<EmpresaConInstalacion?>(null) }

    porEliminar?.let { empresa ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("Eliminar Empresa") },
            text = { Text("¿Estás seguro de eliminar a ${empresa.razonSocial} y su instalación asociada?") },
            confirmButton = {
                TextButton(onClick = {
                    onEliminar(empresa)
                    porEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { porEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listado de Empresas e Instalaciones") },
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CampoTexto(
                valor = filtro,
                onValorCambia = onBuscar,
                etiqueta = "Buscar empresa...",
                icono = R.drawable.ic_buscar,
                modifier = Modifier.padding(
                    horizontal = Dimens.espacioMedio,
                    vertical = Dimens.espacioChico
                )
            )

            if (empresas.isEmpty()) {
                Text(
                    text = "No hay empresas registradas en el sistema.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(Dimens.espacioMedio)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Dimens.espacioMedio),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
            ) {
                items(empresas, key = { it.idEmpresa }) { empresa ->
                    Card {
                        ListItem(
                            overlineContent = { Text("RUT: ${empresa.rut} | Giro: ${empresa.giro}") },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.ic_giro),
                                    contentDescription = null
                                )
                            },
                            headlineContent = { Text(empresa.razonSocial) },
                            supportingContent = { Text("Instalación: ${empresa.nombreInstalacion} (${empresa.comuna})") },
                            trailingContent = {
                                IconButton(onClick = { porEliminar = empresa }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_eliminar),
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}