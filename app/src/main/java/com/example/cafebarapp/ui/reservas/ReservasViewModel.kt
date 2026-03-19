package com.example.cafebarapp.ui.reservas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafebarapp.data.model.NuevaReserva
import com.example.cafebarapp.data.model.Reserva
import com.example.cafebarapp.data.repository.ReservaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ReservasUiState {
    object Loading : ReservasUiState()
    data class Success(val reservas: List<Reserva>) : ReservasUiState()
    data class Error(val mensaje: String) : ReservasUiState()
}

sealed class CrearReservaEstado {
    object Idle : CrearReservaEstado()
    object Enviando : CrearReservaEstado()
    data class Exitoso(val codigo: String) : CrearReservaEstado()
    data class Error(val mensaje: String) : CrearReservaEstado()
}

class ReservasViewModel(private val repository: ReservaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ReservasUiState>(ReservasUiState.Loading)
    val uiState: StateFlow<ReservasUiState> = _uiState

    private val _crearEstado = MutableStateFlow<CrearReservaEstado>(CrearReservaEstado.Idle)
    val crearEstado: StateFlow<CrearReservaEstado> = _crearEstado

    fun cargarReservas(clienteId: Int) {
        viewModelScope.launch {
            _uiState.value = ReservasUiState.Loading
            val result = repository.getReservas(clienteId)
            _uiState.value = result.fold(
                onSuccess = { ReservasUiState.Success(it) },
                onFailure = { ReservasUiState.Error(it.message ?: "Error al cargar reservas") }
            )
        }
    }

    fun crearReserva(clienteId: Int, mesaId: Int, fecha: String, hora: String, personas: Int) {
        if (fecha.isBlank() || hora.isBlank() || personas <= 0) {
            _crearEstado.value = CrearReservaEstado.Error("Complete todos los campos")
            return
        }

        viewModelScope.launch {
            _crearEstado.value = CrearReservaEstado.Enviando
            val nueva = NuevaReserva(clienteId, mesaId, fecha, hora, personas)
            val result = repository.crearReserva(nueva)
            _crearEstado.value = result.fold(
                onSuccess = { CrearReservaEstado.Exitoso(it.codigoConfirmacion) },
                onFailure = { CrearReservaEstado.Error(it.message ?: "Error al crear reserva") }
            )
        }
    }

    fun resetCrearEstado() {
        _crearEstado.value = CrearReservaEstado.Idle
    }
}
