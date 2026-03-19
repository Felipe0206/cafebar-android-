package com.example.cafebarapp.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafebarapp.data.model.Categoria
import com.example.cafebarapp.data.model.DetallePedido
import com.example.cafebarapp.data.model.ItemCarrito
import com.example.cafebarapp.data.model.NuevoPedido
import com.example.cafebarapp.data.model.Producto
import com.example.cafebarapp.data.repository.CategoriaRepository
import com.example.cafebarapp.data.repository.PedidoRepository
import com.example.cafebarapp.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MenuUiState {
    object Loading : MenuUiState()
    data class Success(val productos: List<Producto>) : MenuUiState()
    data class Error(val mensaje: String) : MenuUiState()
}

sealed class PedidoEstado {
    object Idle : PedidoEstado()
    object Enviando : PedidoEstado()
    data class Exitoso(val codigo: String) : PedidoEstado()
    data class Error(val mensaje: String) : PedidoEstado()
}

class MenuViewModel(
    private val productoRepository: ProductoRepository,
    private val pedidoRepository: PedidoRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val uiState: StateFlow<MenuUiState> = _uiState

    private val _carrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val carrito: StateFlow<List<ItemCarrito>> = _carrito

    private val _pedidoEstado = MutableStateFlow<PedidoEstado>(PedidoEstado.Idle)
    val pedidoEstado: StateFlow<PedidoEstado> = _pedidoEstado

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    private val _categoriaSeleccionada = MutableStateFlow<Int?>(null)
    val categoriaSeleccionada: StateFlow<Int?> = _categoriaSeleccionada

    private var todosLosProductos: List<Producto> = emptyList()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.value = MenuUiState.Loading
            // Cargar categorías
            categoriaRepository.getCategorias().onSuccess { cats ->
                _categorias.value = cats.filter { it.activo }
            }
            // Cargar productos
            val result = productoRepository.getProductos()
            result.fold(
                onSuccess = { productos ->
                    todosLosProductos = productos.filter { it.disponible }
                    aplicarFiltro()
                },
                onFailure = {
                    _uiState.value = MenuUiState.Error(it.message ?: "Error al cargar menú")
                }
            )
        }
    }

    fun seleccionarCategoria(idCategoria: Int?) {
        _categoriaSeleccionada.value = idCategoria
        aplicarFiltro()
    }

    private fun aplicarFiltro() {
        val filtro = _categoriaSeleccionada.value
        val filtrados = if (filtro == null) {
            todosLosProductos
        } else {
            todosLosProductos.filter { it.idCategoria == filtro }
        }
        _uiState.value = MenuUiState.Success(filtrados)
    }

    fun agregarAlCarrito(producto: Producto) {
        val actual = _carrito.value.toMutableList()
        val index = actual.indexOfFirst { it.producto.idProducto == producto.idProducto }
        if (index >= 0) {
            actual[index] = actual[index].copy(cantidad = actual[index].cantidad + 1)
        } else {
            actual.add(ItemCarrito(producto, 1))
        }
        _carrito.value = actual
    }

    fun quitarDelCarrito(productoId: Int) {
        val actual = _carrito.value.toMutableList()
        val index = actual.indexOfFirst { it.producto.idProducto == productoId }
        if (index >= 0) {
            if (actual[index].cantidad > 1) {
                actual[index] = actual[index].copy(cantidad = actual[index].cantidad - 1)
            } else {
                actual.removeAt(index)
            }
        }
        _carrito.value = actual
    }

    fun totalCarrito(): Double = _carrito.value.sumOf { it.producto.precio * it.cantidad }

    fun cantidadCarrito(): Int = _carrito.value.sumOf { it.cantidad }

    fun enviarPedido(clienteId: Int) {
        val items = _carrito.value
        if (items.isEmpty()) {
            _pedidoEstado.value = PedidoEstado.Error("El carrito está vacío")
            return
        }
        viewModelScope.launch {
            _pedidoEstado.value = PedidoEstado.Enviando
            val detalles = items.map {
                DetallePedido(it.producto.idProducto, it.cantidad, it.producto.precio)
            }
            val nuevoPedido = NuevoPedido(clienteId = clienteId, detalles = detalles)
            val result = pedidoRepository.crearPedido(nuevoPedido)
            _pedidoEstado.value = result.fold(
                onSuccess = {
                    _carrito.value = emptyList()
                    PedidoEstado.Exitoso(it.codigoPedido)
                },
                onFailure = { PedidoEstado.Error(it.message ?: "Error al enviar pedido") }
            )
        }
    }

    fun resetPedidoEstado() {
        _pedidoEstado.value = PedidoEstado.Idle
    }
}
