package com.example.cafebarapp.ui.pedidos

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.cafebarapp.data.model.Pedido
import com.example.cafebarapp.ui.theme.AzulEstado
import com.example.cafebarapp.ui.theme.Blanco
import com.example.cafebarapp.ui.theme.CafeOscuro
import com.example.cafebarapp.ui.theme.CafeMedio
import com.example.cafebarapp.ui.theme.CafeTexto
import com.example.cafebarapp.ui.theme.Crema
import com.example.cafebarapp.ui.theme.CremaBorde
import com.example.cafebarapp.ui.theme.Dorado
import com.example.cafebarapp.ui.theme.DoradoClaro
import com.example.cafebarapp.ui.theme.NaranjaEstado
import com.example.cafebarapp.ui.theme.RojoError
import com.example.cafebarapp.ui.theme.TextoGris
import com.example.cafebarapp.ui.theme.VerdeExito

@Composable
fun PedidosScreen(viewModel: PedidosViewModel, clienteId: Int) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(clienteId) {
        viewModel.cargarPedidos(clienteId)
    }

    Column(modifier = Modifier.fillMaxSize().background(Crema)) {

        // Header
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mis Pedidos", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DoradoClaro)
                Button(
                    onClick = { viewModel.cargarPedidos(clienteId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Dorado),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("↻ Actualizar", fontSize = 13.sp, color = Blanco)
                }
            }
        }

        when (uiState) {
            is PedidosUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Dorado)
                }
            }
            is PedidosUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text((uiState as PedidosUiState.Error).mensaje, color = RojoError)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.cargarPedidos(clienteId) },
                        colors = ButtonDefaults.buttonColors(containerColor = Dorado)
                    ) { Text("Reintentar", color = Blanco) }
                }
            }
            is PedidosUiState.Success -> {
                val pedidos = (uiState as PedidosUiState.Success).pedidos
                if (pedidos.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🛒", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No tienes pedidos aun.", color = TextoGris, fontSize = 15.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(pedidos) { pedido ->
                            PedidoCard(pedido)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PedidoCard(pedido: Pedido) {
    val estadoColor = when (pedido.estado.lowercase()) {
        "pendiente"       -> NaranjaEstado
        "en_preparacion"  -> AzulEstado
        "listo"           -> VerdeExito
        "cancelado"       -> RojoError
        else              -> TextoGris
    }
    val esListo = pedido.estado.lowercase() == "listo"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Blanco, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        // Alerta verde si listo
        if (esListo) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerdeExito.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    "✓ Tu pedido esta listo. El mesero te lo llevara.",
                    color = VerdeExito,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Pedido #${pedido.codigoPedido}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = CafeOscuro
            )
            // Badge de estado
            Box(
                modifier = Modifier
                    .background(estadoColor.copy(alpha = 0.15f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    pedido.estado.replace("_", " ").uppercase(),
                    color = estadoColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            pedido.fechaPedido?.let {
                Text(it.take(16), fontSize = 12.sp, color = TextoGris)
            }
            Text(
                "$${"%.0f".format(pedido.total)}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Dorado
            )
        }

        pedido.tipoPedido?.let {
            Text("Tipo: $it", fontSize = 12.sp, color = TextoGris)
        }
    }
}
