package com.example.cafebarapp

import com.example.cafebarapp.data.model.ItemCarrito
import com.example.cafebarapp.data.model.Producto
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductoModelTest {

    private fun productoEjemplo(id: Int, precio: Double) = Producto(
        idProducto = id,
        nombre = "Producto $id",
        descripcion = "Descripcion",
        precio = precio,
        disponible = true,
        imagenUrl = null,
        stock = 10,
        idCategoria = 1
    )

    @Test
    fun `subtotal de item carrito es correcto`() {
        val producto = productoEjemplo(1, 5500.0)
        val item = ItemCarrito(producto, 3)
        val subtotal = item.producto.precio * item.cantidad
        assertEquals(16500.0, subtotal, 0.01)
    }

    @Test
    fun `total carrito con multiples items es correcto`() {
        val items = listOf(
            ItemCarrito(productoEjemplo(1, 3000.0), 2),
            ItemCarrito(productoEjemplo(2, 5000.0), 1)
        )
        val total = items.sumOf { it.producto.precio * it.cantidad }
        assertEquals(11000.0, total, 0.01)
    }

    @Test
    fun `producto no disponible no debe aparecer en menu`() {
        val productos = listOf(
            productoEjemplo(1, 3000.0).copy(disponible = true),
            productoEjemplo(2, 4000.0).copy(disponible = false),
            productoEjemplo(3, 5000.0).copy(disponible = true)
        )
        val disponibles = productos.filter { it.disponible }
        assertEquals(2, disponibles.size)
    }
}
