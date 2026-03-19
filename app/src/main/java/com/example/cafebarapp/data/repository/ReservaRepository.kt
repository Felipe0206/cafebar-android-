package com.example.cafebarapp.data.repository

import com.example.cafebarapp.data.model.Mesa
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

    suspend fun cancelarReserva(idReserva: Int): Result<Unit> {
        return try {
            val body = mapOf<String, Any>("idReserva" to idReserva, "estado" to "cancelada")
            val response = apiService.actualizarReserva(body)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al cancelar reserva"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMesasLibres(): Result<List<Mesa>> {
        return try {
            val response = apiService.getMesas("libre")
            if (response.isSuccessful) {
                val body = response.body()
                Result.success(body?.data ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener mesas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
