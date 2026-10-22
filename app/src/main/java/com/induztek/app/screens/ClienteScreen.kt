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
import com.induztek.app.data.ClienteConInstalacion
import com.induztek.app.ui.components.CampoTexto
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.viewmodel.ClientesViewModel

@Composable
fun ClientesScreen(
    viewModel: ClientesViewModel = viewModel(),
    onVolver: () -> Unit
) {
    val clientes by viewModel.clientes.collectAsStateWithLifecycle()
    val filtro by viewModel.filtro.collectAsStateWithLifecycle()

    ClientesContent(
        clientes = clientes,
        onVolver = onVolver,
        filtro = filtro,
        onBuscar = viewModel::buscar,
        onEliminar = viewModel::eliminar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesContent(
    clientes: List<ClienteConInstalacion>,
    onVolver: () -> Unit,
    filtro: String = "",
    onBuscar: (String) -> Unit = {},
    onEliminar: (ClienteConInstalacion) -> Unit = {}
) {
    var porEliminar by remember { mutableStateOf<ClienteConInstalacion?>(null) }

    porEliminar?.let { cliente ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("Eliminar Cliente") },
            text = { Text("¿Estás seguro de eliminar a ${cliente.razonSocial} y su instalación asociada?") },
            confirmButton = {
                TextButton(onClick = {
                    onEliminar(cliente)
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
                title = { Text("Listado de Clientes e Instalaciones") },
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
                etiqueta = "Buscar cliente...",
                icono = R.drawable.ic_buscar,
                modifier = Modifier.padding(
                    horizontal = Dimens.espacioMedio,
                    vertical = Dimens.espacioChico
                )
            )

            if (clientes.isEmpty()) {
                Text(
                    text = "No hay clientes registrados en el sistema.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(Dimens.espacioMedio)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Dimens.espacioMedio),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
            ) {
                items(clientes, key = { it.idCliente }) { cliente ->
                    Card {
                        ListItem(
                            overlineContent = { Text("RUT: ${cliente.rut} | Giro: ${cliente.giro}") },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.ic_giro),
                                    contentDescription = null
                                )
                            },
                            headlineContent = { Text(cliente.razonSocial) },
                            supportingContent = { Text("Instalación: ${cliente.nombreInstalacion} (${cliente.comuna})") },
                            trailingContent = {
                                IconButton(onClick = { porEliminar = cliente }) {
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