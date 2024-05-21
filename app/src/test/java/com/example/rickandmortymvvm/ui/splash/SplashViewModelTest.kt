package com.example.rickandmortymvvm.ui.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.rickandmortymvvm.data.repositories.CommonRepository
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.domain.use_case.GetCharactersUseCase
import io.mockk.coEvery
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.example.rickandmortymvvm.domain.model.CharactersResultModel
import com.example.rickandmortymvvm.domain.model.InfoModel
import com.example.rickandmortymvvm.domain.use_case.CharacterResultList
import com.example.rickandmortymvvm.ui.MainCoroutineRule
import io.mockk.coVerify
import org.mockito.Mock
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class SplashViewModelTest {

    // This rule swaps background executor used by the Architecture Components with a different one which executes each task synchronously.
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getCharactersUseCase: GetCharactersUseCase
    private lateinit var commonRepository: CommonRepository


    @Before
    fun setup() {
        commonRepository = mockk()
        getCharactersUseCase = mockk()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test getCharacters`() = runTest {
        val viewModel = SplashViewModel(getCharactersUseCase, commonRepository)
        // Given
        val infoModel = InfoModel(4, "false", 4, "false")
        val characters = mutableListOf(
            Characters(1, "Pepe", "Vallecano", "qweqwewq"),
            Characters(2, "Juja", "Coloso", "popopop")
        )

        val charactersResultModel = CharactersResultModel(infoModel, characters)
        val flow = flowOf(CharacterResultList.Success(charactersResultModel))
        coEvery { getCharactersUseCase(any()) } returns flow

        // When
        viewModel.getCharacters()

        // Then
        assertEquals(characters, viewModel.state.characters)
        assertEquals(false, viewModel.state.isLoading)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test getCharacters with error`() = runTest {
        val viewModel = SplashViewModel(getCharactersUseCase, commonRepository)
        // Given
        val errorMessage = "Error occurred"
        val flow = flowOf(CharacterResultList.Error(errorMessage))
        coEvery { getCharactersUseCase(any()) } returns flow
        // When
        viewModel.getCharacters()

        // Then
        assertEquals(false, viewModel.state.isLoading)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test updateState`() = runTest {
        val viewModel = SplashViewModel(getCharactersUseCase, commonRepository)
        // Given
        val characters = mutableListOf(
            Characters(1, "Pepe", "Vallecano", "qweqwewq"),
            Characters(2, "Juja", "Coloso", "popopop")
        )

        // When
        viewModel.updateState(characters, isLoading = false)

        // Then
        assertEquals(characters, viewModel.state.characters)
        assertEquals(false, viewModel.state.isLoading)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test handleError`() = runTest {
        val viewModel = SplashViewModel(getCharactersUseCase, commonRepository)
        // When
        viewModel.handleError(isError = true)

        // Then
        assertEquals(true, viewModel.state.isLoading)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test showSnackbarMethod`() = runTest {
        val viewModel = SplashViewModel(getCharactersUseCase, commonRepository)
        // Given
        val message = "Test message"
        val eventList = mutableListOf<SplashViewModel.UIEvent>()

        val eventFlowMock = mockk<MutableSharedFlow<SplashViewModel.UIEvent>>(relaxed = true)
        viewModel.fEventFlow = eventFlowMock

        // When
        viewModel.showSnackbarMethod(message)

        // Then
        coVerify { eventFlowMock.emit(SplashViewModel.UIEvent.ShowSnackbar(message)) }
    }
}