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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
import kotlinx.coroutines.delay

@Composable
fun PedidosScreen(viewModel: PedidosViewModel, clienteId: Int) {
    val uiState by viewModel.uiState.collectAsState()
    var pedidoSeguimiento by remember { mutableStateOf<Pedido?>(null) }

    // Carga inicial + auto-refresh cada 30 segundos
    LaunchedEffect(clienteId) {
        while (true) {
            viewModel.cargarPedidos(clienteId)
            delay(30_000)
        }
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
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Mis Pedidos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DoradoClaro)
                    Text("Actualiza cada 30s", fontSize = 11.sp, color = Blanco.copy(alpha = 0.6f))
                }
                Button(
                    onClick = { viewModel.cargarPedidos(clienteId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Dorado),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("↻ Actualizar", fontSize = 12.sp, color = Blanco)
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
                    Text("😕", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text((uiState as PedidosUiState.Error).mensaje, color = RojoError, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { viewModel.cargarPedidos(clienteId) }, colors = ButtonDefaults.buttonColors(containerColor = Dorado)) {
                        Text("Reintentar", color = Blanco)
                    }
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
                            PedidoCard(
                                pedido = pedido,
                                onVerSeguimiento = { pedidoSeguimiento = pedido }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal seguimiento
    pedidoSeguimiento?.let { pedido ->
        SeguimientoModal(pedido = pedido, onDismiss = { pedidoSeguimiento = null })
    }
}

@Composable
fun PedidoCard(pedido: Pedido, onVerSeguimiento: () -> Unit) {
    val estadoColor = when (pedido.estado.lowercase()) {
        "pendiente"      -> NaranjaEstado
        "en_preparacion" -> AzulEstado
        "listo"          -> VerdeExito
        "cancelado"      -> RojoError
        else             -> TextoGris
    }
    val esListo = pedido.estado.lowercase() == "listo"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Blanco, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        if (esListo) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerdeExito.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text("✓ ¡Tu pedido esta listo! El mesero te lo llevara.", color = VerdeExito, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Pedido #${pedido.codigoPedido}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CafeOscuro)
            Box(
                modifier = Modifier
                    .background(estadoColor.copy(alpha = 0.15f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(pedido.estado.replace("_", " ").uppercase(), color = estadoColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            pedido.fechaPedido?.let { Text(it.take(16), fontSize = 12.sp, color = TextoGris) }
            Text("$${"%.0f".format(pedido.total)}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Dorado)
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onVerSeguimiento,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CafeOscuro),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
        ) {
            Text("Ver seguimiento", color = DoradoClaro, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SeguimientoModal(pedido: Pedido, onDismiss: () -> Unit) {
    val pasos = listOf("Recibido", "Preparando", "Listo", "Entregado")
    val pasoActual = when (pedido.estado.lowercase()) {
        "pendiente"      -> 0
        "en_preparacion" -> 1
        "listo"          -> 2
        "entregado"      -> 3
        else             -> 0
    }

    val headerGradient = when (pedido.estado.lowercase()) {
        "en_preparacion" -> listOf(androidx.compose.ui.graphics.Color(0xFF1A3A5C), AzulEstado)
        "listo"          -> listOf(androidx.compose.ui.graphics.Color(0xFF064E3B), VerdeExito)
        "entregado"      -> listOf(CafeOscuro, Dorado)
        else             -> listOf(CafeOscuro, CafeMedio)
    }

    val headerIcono = when (pedido.estado.lowercase()) {
        "en_preparacion" -> "👨‍🍳"
        "listo"          -> "🔔"
        "entregado"      -> "✅"
        else             -> "⏱"
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Blanco, RoundedCornerShape(16.dp))
        ) {
            // Header dinámico
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(headerGradient), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(72.dp).background(Blanco.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text(headerIcono, fontSize = 36.sp) }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Seguimiento de Pedido", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Blanco)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier.background(Blanco.copy(alpha = 0.2f), RoundedCornerShape(999.dp)).padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("#${pedido.codigoPedido}", color = Blanco, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {

                // Stepper
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    pasos.forEachIndexed { index, paso ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            val completado = index <= pasoActual
                            val activo = index == pasoActual
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        when {
                                            activo -> Dorado
                                            completado -> VerdeExito
                                            else -> CremaBorde
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (completado && !activo) "✓" else "${index + 1}",
                                    color = if (completado || activo) Blanco else TextoGris,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(paso, fontSize = 10.sp, color = if (completado) CafeOscuro else TextoGris, textAlign = TextAlign.Center, fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal)
                        }
                        if (index < pasos.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .weight(0.5f)
                                    .height(3.dp)
                                    .background(if (index < pasoActual) VerdeExito else CremaBorde, RoundedCornerShape(999.dp))
                                    .align(Alignment.Top)
                                    .padding(top = 17.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = CremaBorde)
                Spacer(modifier = Modifier.height(12.dp))

                // Info pedido
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total del pedido", color = TextoGris, fontSize = 14.sp)
                    Text("$${"%.0f".format(pedido.total)}", fontWeight = FontWeight.Bold, color = Dorado, fontSize = 16.sp)
                }

                pedido.tipoPedido?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tipo", color = TextoGris, fontSize = 14.sp)
                        Text(it.replaceFirstChar { c -> c.uppercase() }, fontSize = 14.sp, color = CafeTexto)
                    }
                }

                // Alerta listo
                if (pedido.estado.lowercase() == "listo") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VerdeExito.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text("🔔 ¡Tu pedido esta listo! El mesero se acercara a tu mesa.", color = VerdeExito, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CafeOscuro)
                ) {
                    Text("Cerrar", color = DoradoClaro, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
