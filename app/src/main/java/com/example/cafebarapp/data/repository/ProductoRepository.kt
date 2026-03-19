package com.example.cafebarapp.data.repository

import com.example.cafebarapp.data.model.Producto
import com.example.cafebarapp.data.network.ApiService

class ProductoRepository(private val apiService: ApiService) {

    suspend fun getProductos(): Result<List<Producto>> {
        return try {
            val response = apiService.getProductos()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body?.message ?: "Error al obtener productos"))
                }
            } else {
                Result.failure(Exception("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
