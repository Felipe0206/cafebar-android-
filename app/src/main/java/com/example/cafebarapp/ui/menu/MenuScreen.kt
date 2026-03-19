package com.example.cafebarapp.ui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.cafebarapp.data.model.ItemCarrito
import com.example.cafebarapp.data.model.Producto
import com.example.cafebarapp.ui.theme.AzulAccion
import com.example.cafebarapp.ui.theme.Blanco
import com.example.cafebarapp.ui.theme.CafeOscuro
import com.example.cafebarapp.ui.theme.CafeMedio
import com.example.cafebarapp.ui.theme.CafeTexto
import com.example.cafebarapp.ui.theme.Crema
import com.example.cafebarapp.ui.theme.CremaBorde
import com.example.cafebarapp.ui.theme.Dorado
import com.example.cafebarapp.ui.theme.DoradoClaro
import com.example.cafebarapp.ui.theme.RojoError
import com.example.cafebarapp.ui.theme.TextoGris
import com.example.cafebarapp.ui.theme.VerdeExito

@Composable
fun MenuScreen(
    viewModel: MenuViewModel,
    clienteId: Int,
    nombreUsuario: String,
    onPedidoExitoso: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val carrito by viewModel.carrito.collectAsState()
    val pedidoEstado by viewModel.pedidoEstado.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val categoriaSeleccionada by viewModel.categoriaSeleccionada.collectAsState()

    var mostrarCarrito by remember { mutableStateOf(false) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var codigoPedidoExitoso by remember { mutableStateOf("") }

    LaunchedEffect(pedidoEstado) {
        if (pedidoEstado is PedidoEstado.Exitoso) {
            codigoPedidoExitoso = (pedidoEstado as PedidoEstado.Exitoso).codigo
            mostrarCarrito = false
            mostrarConfirmacion = true
            viewModel.resetPedidoEstado()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Crema)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(CafeOscuro, CafeMedio),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, 0f)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Menu Cafe Bar", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DoradoClaro)
                        Text("Hola, $nombreUsuario", fontSize = 12.sp, color = Blanco.copy(alpha = 0.7f))
                    }
                    if (carrito.isNotEmpty()) {
                        Button(
                            onClick = { mostrarCarrito = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Dorado),
                            shape = RoundedCornerShape(999.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("🛒 ${viewModel.cantidadCarrito()}", color = Blanco, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Barra de categorías
            if (categorias.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Blanco)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pill "Todos"
                    val todosSeleccionado = categoriaSeleccionada == null
                    Button(
                        onClick = { viewModel.seleccionarCategoria(null) },
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (todosSeleccionado) Dorado else Blanco,
                            contentColor = if (todosSeleccionado) Blanco else CafeTexto
                        ),
                        border = BorderStroke(1.dp, if (todosSeleccionado) Dorado else CremaBorde),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Todos", fontSize = 13.sp, fontWeight = if (todosSeleccionado) FontWeight.Bold else FontWeight.Normal)
                    }

                    categorias.forEach { cat ->
                        val seleccionado = categoriaSeleccionada == cat.idCategoria
                        Button(
                            onClick = { viewModel.seleccionarCategoria(cat.idCategoria) },
                            shape = RoundedCornerShape(999.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (seleccionado) Dorado else Blanco,
                                contentColor = if (seleccionado) Blanco else CafeTexto
                            ),
                            border = BorderStroke(1.dp, if (seleccionado) Dorado else CremaBorde),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(cat.nombre, fontSize = 13.sp, fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }

            // Contenido principal
            when (uiState) {
                is MenuUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Dorado)
                    }
                }
                is MenuUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("😕", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text((uiState as MenuUiState.Error).mensaje, color = RojoError, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.cargarDatos() }, colors = ButtonDefaults.buttonColors(containerColor = Dorado)) {
                            Text("Reintentar", color = Blanco)
                        }
                    }
                }
                is MenuUiState.Success -> {
                    val productos = (uiState as MenuUiState.Success).productos
                    if (productos.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("☕", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No hay productos en esta categoria.", color = TextoGris)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(productos) { producto ->
                                ProductoCard(
                                    producto = producto,
                                    onAgregar = { viewModel.agregarAlCarrito(producto) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
            }
        }

        // Botón flotante carrito (abajo a la derecha)
        if (carrito.isNotEmpty()) {
            Button(
                onClick = { mostrarCarrito = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulAccion),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text(
                    "Ver pedido (${viewModel.cantidadCarrito()}) · $${"%.0f".format(viewModel.totalCarrito())}",
                    color = Blanco,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }

    // Modal carrito
    if (mostrarCarrito) {
        CarritoModal(
            carrito = carrito,
            total = viewModel.totalCarrito(),
            pedidoEstado = pedidoEstado,
            onQuitar = { viewModel.quitarDelCarrito(it) },
            onConfirmar = { viewModel.enviarPedido(clienteId) },
            onDismiss = { mostrarCarrito = false }
        )
    }

    // Modal confirmación pedido exitoso
    if (mostrarConfirmacion) {
        PedidoConfirmadoModal(
            codigo = codigoPedidoExitoso,
            onDismiss = {
                mostrarConfirmacion = false
                onPedidoExitoso(codigoPedidoExitoso)
            }
        )
    }
}

@Composable
fun ProductoCard(producto: Producto, onAgregar: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Blanco, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(Crema, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("☕", fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CafeOscuro, maxLines = 1, overflow = TextOverflow.Ellipsis)
                producto.descripcion?.let {
                    Text(it, fontSize = 12.sp, color = TextoGris, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("$${"%.0f".format(producto.precio)}", color = Dorado, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = onAgregar,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.5.dp, CafeOscuro),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("+ Agregar", fontSize = 12.sp, color = CafeOscuro, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CarritoModal(
    carrito: List<ItemCarrito>,
    total: Double,
    pedidoEstado: PedidoEstado,
    onQuitar: (Int) -> Unit,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Blanco, RoundedCornerShape(16.dp))
        ) {
            // Header naranja
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(listOf(CafeOscuro, CafeMedio)),
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text("Tu Pedido", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DoradoClaro)
            }

            Column(modifier = Modifier.padding(16.dp)) {
                carrito.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.producto.nombre, modifier = Modifier.weight(1f), fontSize = 14.sp, color = CafeTexto)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onQuitar(item.producto.idProducto) },
                                modifier = Modifier.size(28.dp).background(CremaBorde, CircleShape)
                            ) { Text("−", fontSize = 14.sp, color = CafeOscuro, textAlign = TextAlign.Center) }
                            Text(
                                " ${item.cantidad} ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .background(DoradoClaro.copy(alpha = 0.2f), RoundedCornerShape(999.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("x${item.cantidad}", fontSize = 12.sp, color = CafeOscuro)
                            }
                        }
                        Text(
                            "$${"%.0f".format(item.producto.precio * item.cantidad)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Dorado,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                HorizontalDivider(color = CremaBorde, modifier = Modifier.padding(vertical = 8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("$${"%.0f".format(total)}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Dorado)
                }

                if (pedidoEstado is PedidoEstado.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().background(RojoError.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(8.dp)
                    ) {
                        Text((pedidoEstado as PedidoEstado.Error).mensaje, color = RojoError, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CremaBorde)
                    ) { Text("Seguir viendo", color = TextoGris, fontSize = 13.sp) }

                    Button(
                        onClick = onConfirmar,
                        modifier = Modifier.weight(1f),
                        enabled = pedidoEstado !is PedidoEstado.Enviando,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Dorado)
                    ) {
                        if (pedidoEstado is PedidoEstado.Enviando) {
                            CircularProgressIndicator(color = Blanco, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        } else {
                            Text("Confirmar Pedido", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PedidoConfirmadoModal(codigo: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Blanco, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(VerdeExito.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", fontSize = 40.sp, color = VerdeExito)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("¡Pedido Enviado!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CafeOscuro)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tu pedido ha sido recibido y está siendo preparado.", fontSize = 14.sp, color = TextoGris, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .background(Crema, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Codigo: #$codigo", fontWeight = FontWeight.Bold, color = CafeOscuro, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Dorado)
            ) {
                Text("Ver mis pedidos", color = Blanco, fontWeight = FontWeight.Bold)
            }
        }
    }
}
