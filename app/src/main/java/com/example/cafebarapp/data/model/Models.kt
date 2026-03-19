package com.example.cafebarapp.data.model

import com.google.gson.annotations.SerializedName

// --- Respuestas genéricas de la API ---

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("count") val count: Int = 0,
    @SerializedName("message") val message: String? = null
)

data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("usuario") val usuario: Usuario?,
    @SerializedName("message") val message: String? = null
)

// --- Modelos del dominio ---

data class Usuario(
    @SerializedName("idUsuario") val idUsuario: Int,
    @SerializedName("clienteId") val clienteId: Int? = null,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("activo") val activo: Boolean
)

data class Categoria(
    @SerializedName("idCategoria") val idCategoria: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("icono") val icono: String?,
    @SerializedName("activo") val activo: Boolean
)

data class Producto(
    @SerializedName("idProducto") val idProducto: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("precio") val precio: Double,
    @SerializedName("disponible") val disponible: Boolean,
    @SerializedName("imagenUrl") val imagenUrl: String?,
    @SerializedName("stock") val stock: Int,
    @SerializedName("idCategoria") val idCategoria: Int?
)

data class Mesa(
    @SerializedName("idMesa") val idMesa: Int,
    @SerializedName("numeroMesa") val numeroMesa: Int,
    @SerializedName("capacidad") val capacidad: Int,
    @SerializedName("ubicacion") val ubicacion: String?,
    @SerializedName("estado") val estado: String
)

data class Pedido(
    @SerializedName("idPedido") val idPedido: Int,
    @SerializedName("codigoPedido") val codigoPedido: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("total") val total: Double,
    @SerializedName("tipoPedido") val tipoPedido: String?,
    @SerializedName("observaciones") val observaciones: String?,
    @SerializedName("fechaPedido") val fechaPedido: String?,
    @SerializedName("prioridad") val prioridad: String?
)

data class DetallePedido(
    @SerializedName("idProducto") val idProducto: Int,
    @SerializedName("cantidad") val cantidad: Int,
    @SerializedName("precioUnitario") val precioUnitario: Double
)

data class NuevoPedido(
    @SerializedName("clienteId") val clienteId: Int,
    @SerializedName("mesaId") val mesaId: Int? = null,
    @SerializedName("tipoPedido") val tipoPedido: String = "mesa",
    @SerializedName("observaciones") val observaciones: String = "",
    @SerializedName("detalles") val detalles: List<DetallePedido>
)

data class Reserva(
    @SerializedName("idReserva") val idReserva: Int,
    @SerializedName("codigoConfirmacion") val codigoConfirmacion: String,
    @SerializedName("fechaReserva") val fechaReserva: String,
    @SerializedName("horaReserva") val horaReserva: String,
    @SerializedName("numeroPersonas") val numeroPersonas: Int,
    @SerializedName("estado") val estado: String,
    @SerializedName("duracionEstimada") val duracionEstimada: Int?
)

data class NuevaReserva(
    @SerializedName("clienteId") val clienteId: Int,
    @SerializedName("mesaId") val mesaId: Int,
    @SerializedName("fechaReserva") val fechaReserva: String,
    @SerializedName("horaReserva") val horaReserva: String,
    @SerializedName("numeroPersonas") val numeroPersonas: Int,
    @SerializedName("duracionEstimada") val duracionEstimada: Int = 60
)

// Modelo del carrito (solo local)
data class ItemCarrito(
    val producto: Producto,
    val cantidad: Int
)
