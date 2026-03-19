package com.example.cafebarapp.data.repository

import com.example.cafebarapp.data.model.LoginResponse
import com.example.cafebarapp.data.network.ApiService

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val body = mapOf("email" to email, "password" to password)
            val response = apiService.login(body)
            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse != null && loginResponse.success) {
                    Result.success(loginResponse)
                } else {
                    Result.failure(Exception(loginResponse?.message ?: "Credenciales incorrectas"))
                }
            } else {
                Result.failure(Exception("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
