package com.example.cafebarapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cafebarapp.data.model.Usuario
import com.example.cafebarapp.data.network.RetrofitClient
import com.example.cafebarapp.data.repository.AuthRepository
import com.example.cafebarapp.data.repository.CategoriaRepository
import com.example.cafebarapp.data.repository.PedidoRepository
import com.example.cafebarapp.data.repository.ProductoRepository
import com.example.cafebarapp.data.repository.ReservaRepository
import com.example.cafebarapp.ui.login.LoginScreen
import com.example.cafebarapp.ui.login.LoginViewModel
import com.example.cafebarapp.ui.menu.MenuScreen
import com.example.cafebarapp.ui.menu.MenuViewModel
import com.example.cafebarapp.ui.pedidos.PedidosScreen
import com.example.cafebarapp.ui.pedidos.PedidosViewModel
import com.example.cafebarapp.ui.reservas.ReservasScreen
import com.example.cafebarapp.ui.reservas.ReservasViewModel
import com.example.cafebarapp.ui.theme.CafeOscuro
import com.example.cafebarapp.ui.theme.Dorado
import com.example.cafebarapp.ui.theme.TextoGris

object Rutas {
    const val MENU = "menu"
    const val PEDIDOS = "pedidos"
    const val RESERVAS = "reservas"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }

    val apiService = RetrofitClient.apiService

    val loginViewModel = remember { LoginViewModel(AuthRepository(apiService)) }
    val menuViewModel = remember {
        MenuViewModel(ProductoRepository(apiService), PedidoRepository(apiService), CategoriaRepository(apiService))
    }
    val pedidosViewModel = remember { PedidosViewModel(PedidoRepository(apiService)) }
    val reservasViewModel = remember { ReservasViewModel(ReservaRepository(apiService)) }

    if (usuarioActual == null) {
        LoginScreen(
            viewModel = loginViewModel,
            onLoginExitoso = { usuario -> usuarioActual = usuario }
        )
    } else {
        val usuario = usuarioActual!!
        val clienteId = usuario.clienteId ?: usuario.idUsuario

        ClienteScaffold(navController = navController) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Rutas.MENU,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Rutas.MENU) {
                    MenuScreen(
                        viewModel = menuViewModel,
                        clienteId = clienteId,
                        nombreUsuario = usuario.nombre,
                        onPedidoExitoso = { navController.navigate(Rutas.PEDIDOS) }
                    )
                }
                composable(Rutas.PEDIDOS) {
                    PedidosScreen(viewModel = pedidosViewModel, clienteId = clienteId)
                }
                composable(Rutas.RESERVAS) {
                    ReservasScreen(
                        viewModel = reservasViewModel,
                        clienteId = clienteId,
                        onReservaExitosa = { }
                    )
                }
            }
        }
    }
}

@Composable
fun ClienteScaffold(
    navController: NavHostController,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val tabs = listOf(
        Triple(Rutas.MENU, "☕", "Menu"),
        Triple(Rutas.PEDIDOS, "🛒", "Pedidos"),
        Triple(Rutas.RESERVAS, "📅", "Reservas")
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = CafeOscuro) {
                tabs.forEach { (ruta, icono, label) ->
                    val seleccionado = rutaActual == ruta
                    NavigationBarItem(
                        selected = seleccionado,
                        onClick = {
                            navController.navigate(ruta) {
                                popUpTo(Rutas.MENU) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(label, fontSize = if (seleccionado) 12.sp else 11.sp) },
                        icon = { Text(icono, fontSize = 20.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = Dorado,
                            unselectedTextColor = TextoGris,
                            indicatorColor = Dorado.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        },
        content = content
    )
}

private val Int.sp get() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)
private val Float.sp get() = androidx.compose.ui.unit.TextUnit(this, androidx.compose.ui.unit.TextUnitType.Sp)
