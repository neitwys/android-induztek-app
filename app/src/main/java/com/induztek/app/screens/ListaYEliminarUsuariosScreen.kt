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
import com.induztek.app.model.Usuario
import com.induztek.app.ui.components.CampoTexto
import com.induztek.app.ui.theme.Dimens
import com.induztek.app.viewmodel.ListaYEliminarUsuariosViewModel

@Composable
fun ListaYEliminarUsuariosScreen(
    idUsuarioActual: Long,
    viewModel: ListaYEliminarUsuariosViewModel = viewModel(),
    onVolver: () -> Unit
) {
    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()
    val filtro by viewModel.filtro.collectAsStateWithLifecycle()

    ListaYEliminarUsuariosContent(
        usuarios = usuarios,
        idUsuarioActual = idUsuarioActual,
        onVolver = onVolver,
        filtro = filtro,
        onBuscar = viewModel::buscar,
        onEliminar = viewModel::eliminar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaYEliminarUsuariosContent(
    usuarios: List<Usuario>,
    idUsuarioActual: Long,
    onVolver: () -> Unit,
    filtro: String = "",
    onBuscar: (String) -> Unit = {},
    onEliminar: (Usuario) -> Unit = {}
) {
    var porEliminar by remember { mutableStateOf<Usuario?>(null) }

    porEliminar?.let { usuario ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("Eliminar Usuario") },
            text = { Text("¿Estás seguro de eliminar al usuario ${usuario.nombre} (${usuario.correo})?") },
            confirmButton = {
                TextButton(onClick = {
                    onEliminar(usuario)
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
                title = { Text("Gestión de Personal y Usuarios") },
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
                etiqueta = "Buscar por nombre, correo o rol...",
                icono = R.drawable.ic_buscar,
                modifier = Modifier.padding(
                    horizontal = Dimens.espacioMedio,
                    vertical = Dimens.espacioChico
                )
            )

            if (usuarios.isEmpty()) {
                Text(
                    text = "No hay usuarios registrados en el sistema.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(Dimens.espacioMedio)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Dimens.espacioMedio),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
            ) {
                items(usuarios, key = { it.id }) { usuario ->
                    val esYo = usuario.id == idUsuarioActual

                    Card {
                        ListItem(
                            overlineContent = { Text("Rol: ${usuario.rolNombre} ${if (usuario.empresaNombre != null) "• Empresa: ${usuario.empresaNombre}" else ""}") },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.ic_person),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            headlineContent = { Text(usuario.nombre) },
                            supportingContent = { Text(usuario.correo) },
                            trailingContent = {
                                if (!esYo) {
                                    IconButton(onClick = { porEliminar = usuario }) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_eliminar),
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}