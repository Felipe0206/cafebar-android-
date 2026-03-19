package com.example.cafebarapp.data.repository

import com.example.cafebarapp.data.model.NuevoPedido
import com.example.cafebarapp.data.model.Pedido
import com.example.cafebarapp.data.network.ApiService

class PedidoRepository(private val apiService: ApiService) {

    suspend fun getPedidos(clienteId: Int): Result<List<Pedido>> {
        return try {
            val response = apiService.getPedidos(clienteId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body?.message ?: "Error al obtener pedidos"))
                }
            } else {
                Result.failure(Exception("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearPedido(pedido: NuevoPedido): Result<Pedido> {
        return try {
            val response = apiService.crearPedido(pedido)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Error al crear pedido"))
                }
            } else {
                Result.failure(Exception("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
