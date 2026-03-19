package com.example.cafebarapp.data.repository

import com.example.cafebarapp.data.model.NuevaReserva
import com.example.cafebarapp.data.model.Reserva
import com.example.cafebarapp.data.network.ApiService

class ReservaRepository(private val apiService: ApiService) {

    suspend fun getReservas(clienteId: Int): Result<List<Reserva>> {
        return try {
            val response = apiService.getReservas(clienteId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body?.message ?: "Error al obtener reservas"))
                }
            } else {
                Result.failure(Exception("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearReserva(reserva: NuevaReserva): Result<Reserva> {
        return try {
            val response = apiService.crearReserva(reserva)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Error al crear reserva"))
                }
            } else {
                Result.failure(Exception("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
