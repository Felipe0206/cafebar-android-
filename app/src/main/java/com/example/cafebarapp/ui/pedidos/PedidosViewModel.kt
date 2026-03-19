package com.example.cafebarapp.ui.pedidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafebarapp.data.model.Pedido
import com.example.cafebarapp.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PedidosUiState {
    object Loading : PedidosUiState()
    data class Success(val pedidos: List<Pedido>) : PedidosUiState()
    data class Error(val mensaje: String) : PedidosUiState()
}

class PedidosViewModel(private val repository: PedidoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<PedidosUiState>(PedidosUiState.Loading)
    val uiState: StateFlow<PedidosUiState> = _uiState

    fun cargarPedidos(clienteId: Int) {
        viewModelScope.launch {
            _uiState.value = PedidosUiState.Loading
            val result = repository.getPedidos(clienteId)
            _uiState.value = result.fold(
                onSuccess = { PedidosUiState.Success(it) },
                onFailure = { PedidosUiState.Error(it.message ?: "Error al cargar pedidos") }
            )
        }
    }
}
