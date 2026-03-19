package com.example.cafebarapp

import com.example.cafebarapp.data.model.LoginResponse
import com.example.cafebarapp.data.model.Usuario
import com.example.cafebarapp.data.repository.AuthRepository
import com.example.cafebarapp.ui.login.LoginUiState
import com.example.cafebarapp.ui.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = LoginViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login con campos vacios muestra error`() {
        viewModel.login("", "")
        val state = viewModel.uiState.value
        assertTrue(state is LoginUiState.Error)
        assertEquals("Ingrese email y contraseña", (state as LoginUiState.Error).mensaje)
    }

    @Test
    fun `login exitoso actualiza estado a Success`() = runTest {
        val usuario = Usuario(1, 1, "Juan", "juan@test.com", "cliente", true)
        val loginResponse = LoginResponse(true, usuario)
        whenever(repository.login("juan@test.com", "123456"))
            .thenReturn(Result.success(loginResponse))

        viewModel.login("juan@test.com", "123456")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is LoginUiState.Success)
        assertEquals(usuario, (state as LoginUiState.Success).usuario)
    }

    @Test
    fun `login fallido actualiza estado a Error`() = runTest {
        whenever(repository.login("mal@test.com", "wrong"))
            .thenReturn(Result.failure(Exception("Credenciales incorrectas")))

        viewModel.login("mal@test.com", "wrong")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is LoginUiState.Error)
        assertEquals("Credenciales incorrectas", (state as LoginUiState.Error).mensaje)
    }

    @Test
    fun `resetState regresa a estado Idle`() = runTest {
        viewModel.login("", "")
        viewModel.resetState()
        assertTrue(viewModel.uiState.value is LoginUiState.Idle)
    }
}
