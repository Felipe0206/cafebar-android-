package com.example.cafebarapp.ui.reservas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.cafebarapp.data.model.Reserva
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
fun ReservasScreen(
    viewModel: ReservasViewModel,
    clienteId: Int,
    onReservaExitosa: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val crearEstado by viewModel.crearEstado.collectAsState()
    val mesas by viewModel.mesas.collectAsState()
    var mostrarModal by remember { mutableStateOf(false) }
    var reservaACancelar by remember { mutableStateOf<Reserva?>(null) }

    LaunchedEffect(clienteId) {
        viewModel.cargarReservas(clienteId)
        viewModel.cargarMesas()
    }

    LaunchedEffect(crearEstado) {
        if (crearEstado is CrearReservaEstado.Exitoso) {
            mostrarModal = false
            onReservaExitosa((crearEstado as CrearReservaEstado.Exitoso).codigo)
            viewModel.cargarReservas(clienteId)
            viewModel.resetCrearEstado()
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
                Text("Mis Reservas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DoradoClaro)
                Button(
                    onClick = { mostrarModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Dorado),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("+ Nueva Reserva", fontSize = 13.sp, color = Blanco, fontWeight = FontWeight.Bold)
                }
            }
        }

        when (uiState) {
            is ReservasUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Dorado)
                }
            }
            is ReservasUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text((uiState as ReservasUiState.Error).mensaje, color = RojoError, textAlign = TextAlign.Center)
                }
            }
            is ReservasUiState.Success -> {
                val reservas = (uiState as ReservasUiState.Success).reservas
                if (reservas.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📅", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No tienes reservas.", color = TextoGris, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { mostrarModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Dorado),
                                shape = RoundedCornerShape(8.dp)
                            ) { Text("Crear primera reserva", color = Blanco) }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(reservas) { reserva ->
                            ReservaCard(
                                reserva = reserva,
                                onCancelar = { reservaACancelar = reserva }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal nueva reserva
    if (mostrarModal) {
        NuevaReservaModal(
            crearEstado = crearEstado,
            mesas = mesas,
            onDismiss = { mostrarModal = false; viewModel.resetCrearEstado() },
            onConfirmar = { fecha, hora, personas, mesaId ->
                viewModel.crearReserva(clienteId, mesaId, fecha, hora, personas)
            }
        )
    }

    // Diálogo confirmar cancelación
    reservaACancelar?.let { reserva ->
        AlertDialog(
            onDismissRequest = { reservaACancelar = null },
            title = { Text("Cancelar reserva", fontWeight = FontWeight.Bold) },
            text = { Text("¿Seguro que deseas cancelar la reserva del ${reserva.fechaReserva} a las ${reserva.horaReserva}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelarReserva(reserva.idReserva, clienteId)
                        reservaACancelar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoError)
                ) { Text("Sí, cancelar", color = Blanco) }
            },
            dismissButton = {
                TextButton(onClick = { reservaACancelar = null }) { Text("No", color = TextoGris) }
            }
        )
    }
}

@Composable
fun NuevaReservaModal(
    crearEstado: CrearReservaEstado,
    mesas: List<com.example.cafebarapp.data.model.Mesa>,
    onDismiss: () -> Unit,
    onConfirmar: (String, String, Int, Int) -> Unit
) {
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var personas by remember { mutableStateOf("") }
    var mesaIdSeleccionada by remember { mutableIntStateOf(if (mesas.isNotEmpty()) mesas[0].idMesa else 1) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Blanco, RoundedCornerShape(16.dp))
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(CafeOscuro, CafeMedio)), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .padding(16.dp)
            ) {
                Text("Nueva Reserva", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DoradoClaro)
            }

            Column(modifier = Modifier.padding(16.dp)) {

                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha (AAAA-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Dorado, unfocusedBorderColor = CremaBorde)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = hora,
                    onValueChange = { hora = it },
                    label = { Text("Hora (HH:MM)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Dorado, unfocusedBorderColor = CremaBorde)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = personas,
                    onValueChange = { personas = it },
                    label = { Text("Personas (1-20)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Dorado, unfocusedBorderColor = CremaBorde)
                )

                // Selección de mesa
                if (mesas.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Selecciona una mesa libre:", fontSize = 13.sp, color = CafeTexto, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(mesas) { mesa ->
                            val seleccionada = mesaIdSeleccionada == mesa.idMesa
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (seleccionada) Dorado.copy(alpha = 0.1f) else Blanco,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        BorderStroke(if (seleccionada) 2.dp else 1.dp, if (seleccionada) Dorado else CremaBorde),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { mesaIdSeleccionada = mesa.idMesa }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Mesa", fontSize = 11.sp, color = TextoGris)
                                    Text("${mesa.numeroMesa}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (seleccionada) Dorado else CafeOscuro)
                                    mesa.ubicacion?.let { Text(it, fontSize = 10.sp, color = TextoGris) }
                                }
                            }
                        }
                    }
                }

                if (crearEstado is CrearReservaEstado.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().background(RojoError.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(10.dp)
                    ) {
                        Text((crearEstado as CrearReservaEstado.Error).mensaje, color = RojoError, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = CremaBorde)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar", color = TextoGris)
                    }
                    Button(
                        onClick = { onConfirmar(fecha, hora, personas.toIntOrNull() ?: 0, mesaIdSeleccionada) },
                        modifier = Modifier.weight(1f),
                        enabled = crearEstado !is CrearReservaEstado.Enviando,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Dorado)
                    ) {
                        if (crearEstado is CrearReservaEstado.Enviando) {
                            CircularProgressIndicator(color = Blanco, strokeWidth = 2.dp)
                        } else {
                            Text("Crear Reserva", color = Blanco, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReservaCard(reserva: Reserva, onCancelar: () -> Unit) {
    val cancelable = reserva.estado.lowercase() !in listOf("cancelada", "completada")
    val (icono, color) = when (reserva.estado.lowercase()) {
        "confirmada", "completada" -> "✓" to VerdeExito
        "cancelada"                -> "✕" to RojoError
        else                       -> "⏱" to NaranjaEstado
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Blanco, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icono, fontSize = 18.sp, color = color)
                Spacer(modifier = Modifier.padding(4.dp))
                Text("Cod: ${reserva.codigoConfirmacion}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CafeOscuro)
            }
            Box(
                modifier = Modifier.background(color.copy(alpha = 0.15f), RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(reserva.estado.uppercase(), color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = CremaBorde)
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("📅 ${reserva.fechaReserva}", fontSize = 13.sp, color = CafeTexto)
                Text("🕐 ${reserva.horaReserva}", fontSize = 13.sp, color = CafeTexto)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("👥 ${reserva.numeroPersonas} personas", fontSize = 13.sp, color = TextoGris)
                reserva.duracionEstimada?.let {
                    Text("⏳ ${it} min", fontSize = 12.sp, color = TextoGris)
                }
            }
        }

        if (cancelable) {
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(
                onClick = onCancelar,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, RojoError.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancelar reserva", color = RojoError, fontSize = 13.sp)
            }
        }
    }
}
