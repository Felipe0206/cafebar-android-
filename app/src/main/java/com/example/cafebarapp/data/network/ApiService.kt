package com.example.cafebarapp.data.network

import com.example.cafebarapp.data.model.ApiResponse
import com.example.cafebarapp.data.model.Categoria
import com.example.cafebarapp.data.model.LoginResponse
import com.example.cafebarapp.data.model.Mesa
import com.example.cafebarapp.data.model.NuevoPedido
import com.example.cafebarapp.data.model.NuevaReserva
import com.example.cafebarapp.data.model.Pedido
import com.example.cafebarapp.data.model.Producto
import com.example.cafebarapp.data.model.Reserva
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {

    @POST("login")
    suspend fun login(@Body body: Map<String, String>): Response<LoginResponse>

    @GET("productos")
    suspend fun getProductos(): Response<ApiResponse<List<Producto>>>

    @GET("categorias")
    suspend fun getCategorias(): Response<ApiResponse<List<Categoria>>>

    @GET("mesas")
    suspend fun getMesas(@Query("estado") estado: String = "libre"): Response<ApiResponse<List<Mesa>>>

    @GET("pedidos")
    suspend fun getPedidos(@Query("clienteId") clienteId: Int): Response<ApiResponse<List<Pedido>>>

    @POST("pedidos")
    suspend fun crearPedido(@Body pedido: NuevoPedido): Response<ApiResponse<Pedido>>

    @GET("reservas")
    suspend fun getReservas(@Query("clienteId") clienteId: Int): Response<ApiResponse<List<Reserva>>>

    @POST("reservas")
    suspend fun crearReserva(@Body reserva: NuevaReserva): Response<ApiResponse<Reserva>>

    @PUT("reservas")
    suspend fun actualizarReserva(@Body body: Map<String, Any>): Response<ApiResponse<Reserva>>
}
