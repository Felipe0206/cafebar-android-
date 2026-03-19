package com.example.cafebarapp.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onPedidoExitoso: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val carrito by viewModel.carrito.collectAsState()
    val pedidoEstado by viewModel.pedidoEstado.collectAsState()

    LaunchedEffect(pedidoEstado) {
        if (pedidoEstado is PedidoEstado.Exitoso) {
            onPedidoExitoso((pedidoEstado as PedidoEstado.Exitoso).codigo)
            viewModel.resetPedidoEstado()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Crema)) {

        // Header con gradiente café
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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Menu Cafe Bar",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DoradoClaro
            )
        }

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
                    Text(
                        (uiState as MenuUiState.Error).mensaje,
                        color = RojoError,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.cargarProductos() },
                        colors = ButtonDefaults.buttonColors(containerColor = Dorado)
                    ) { Text("Reintentar", color = Blanco) }
                }
            }
            is MenuUiState.Success -> {
                val productos = (uiState as MenuUiState.Success).productos
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(productos) { producto ->
                        ProductoCard(
                            producto = producto,
                            onAgregar = { viewModel.agregarAlCarrito(producto) }
                        )
                    }
                }
            }
        }

        // Sección carrito
        if (carrito.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Blanco)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Carrito", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CafeOscuro)
                    Text(
                        "${carrito.size} items",
                        fontSize = 13.sp,
                        color = TextoGris
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = CremaBorde)
                Spacer(modifier = Modifier.height(8.dp))

                carrito.forEach { item ->
                    CarritoItem(
                        item = item,
                        onQuitar = { viewModel.quitarDelCarrito(item.producto.idProducto) }
                    )
                }

                HorizontalDivider(color = CremaBorde, modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "$${"%.0f".format(viewModel.totalCarrito())}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Dorado
                    )
                }

                if (pedidoEstado is PedidoEstado.Error) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        (pedidoEstado as PedidoEstado.Error).mensaje,
                        color = RojoError,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { viewModel.enviarPedido(clienteId) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = pedidoEstado !is PedidoEstado.Enviando,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Dorado)
                ) {
                    if (pedidoEstado is PedidoEstado.Enviando) {
                        CircularProgressIndicator(color = Blanco, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Confirmar Pedido", fontWeight = FontWeight.Bold, color = Blanco)
                    }
                }
            }
        }
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
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Placeholder imagen
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Crema, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("☕", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    producto.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = CafeOscuro
                )
                producto.descripcion?.let {
                    Text(it, fontSize = 12.sp, color = TextoGris, maxLines = 2)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "$${"%.0f".format(producto.precio)}",
                    color = Dorado,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = onAgregar,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CafeOscuro)
            ) {
                Text("+ Agregar", fontSize = 12.sp, color = CafeOscuro)
            }
        }
    }
}

@Composable
fun CarritoItem(item: ItemCarrito, onQuitar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item.producto.nombre, modifier = Modifier.weight(1f), fontSize = 14.sp, color = CafeTexto)
        Text(
            "x${item.cantidad}",
            fontSize = 13.sp,
            color = TextoGris,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Text(
            "$${"%.0f".format(item.producto.precio * item.cantidad)}",
            fontSize = 14.sp,
            color = Dorado,
            fontWeight = FontWeight.Medium
        )
        IconButton(
            onClick = onQuitar,
            modifier = Modifier
                .size(28.dp)
                .background(CremaBorde, CircleShape)
        ) {
            Text("−", fontSize = 16.sp, color = CafeOscuro)
        }
    }
}
