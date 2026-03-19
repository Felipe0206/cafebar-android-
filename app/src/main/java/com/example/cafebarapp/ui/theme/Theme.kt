package com.example.cafebarapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CafeBarColorScheme = lightColorScheme(
    primary          = Dorado,
    onPrimary        = Blanco,
    secondary        = CafeOscuro,
    onSecondary      = DoradoClaro,
    tertiary         = AzulAccion,
    background       = Crema,
    onBackground     = CafeTexto,
    surface          = Blanco,
    onSurface        = CafeTexto,
    surfaceVariant   = CremaBorde,
    onSurfaceVariant = TextoGris,
    error            = RojoError,
    outline          = CremaBorde
)

@Composable
fun CafeBarAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CafeBarColorScheme,
        typography  = Typography,
        content     = content
    )
}
