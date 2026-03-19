package com.example.cafebarapp.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton que provee la instancia de Retrofit configurada.
 * En el emulador, 10.0.2.2 apunta al localhost de la máquina host.
 * El backend corre en Tomcat 9 en el puerto 8080, app context /cafebar.
 */
object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/cafebar/api/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
