package com.example.cafebarapp.data.repository

import com.example.cafebarapp.data.model.Categoria
import com.example.cafebarapp.data.network.ApiService

class CategoriaRepository(private val apiService: ApiService) {

    suspend fun getCategorias(): Result<List<Categoria>> {
        return try {
            val response = apiService.getCategorias()
            if (response.isSuccessful) {
                val body = response.body()
                Result.success(body?.data ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener categorías"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
