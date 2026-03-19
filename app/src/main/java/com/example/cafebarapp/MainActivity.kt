package com.example.cafebarapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cafebarapp.navigation.AppNavGraph
import com.example.cafebarapp.ui.theme.CafeBarAppTheme

/**
 * Actividad principal del sistema Cafe Bar.
 * Punto de entrada de la aplicacion Android.
 * Delega la navegacion y la UI a AppNavGraph (Jetpack Compose).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Mostrar mensaje de bienvenida al Cafe Bar
        Toast.makeText(this, "Bienvenido al Cafe Bar!", Toast.LENGTH_LONG).show()
        setContent {
            CafeBarAppTheme {
                AppNavGraph()
            }
        }
    }
}
