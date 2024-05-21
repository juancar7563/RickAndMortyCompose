package com.example.rickandmortymvvm.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.domain.use_case.GetCharacterUseCase
import io.mockk.coEvery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import com.example.rickandmortymvvm.domain.model.Character
import io.mockk.verify
import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.data.source.remote.dto.Location
import com.example.rickandmortymvvm.data.source.remote.dto.Origin
import com.example.rickandmortymvvm.domain.use_case.CharacterResult
import com.example.rickandmortymvvm.ui.MainCoroutineRule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.rules.TestRule
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var getCharacterUseCase: GetCharacterUseCase

    @Before
    fun setup() {
        getCharacterUseCase = mockk()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test getCharacter success`() = runTest {
        val savedStateHandle = mockk<SavedStateHandle>()
        every { savedStateHandle.get<Int>("id") } returns 1
        val viewModel = DetailViewModel(getCharacterUseCase, savedStateHandle)

        // Given
        val characterId = 1
        val character = Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            gender = "Male",
            origin = Origin(name = "Earth (C-137)", url = "https://rickandmortyapi.com/api/location/1"),
            location = Location(name = "Earth (Replacement Dimension)", url = "https://rickandmortyapi.com/api/location/20"),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
        )
        val flow = flowOf(CharacterResult.Success(character))
        coEvery { getCharacterUseCase(characterId) } returns flow

        // When
        viewModel.getCharacter()

        // Then
        assertEquals(character, viewModel.state.character)
        assertEquals(false, viewModel.state.isLoading)
        coVerify { getCharacterUseCase(characterId) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test getCharacter error`() = runTest {
        val savedStateHandle = mockk<SavedStateHandle>()
        every { savedStateHandle.get<Int>("id") } returns 1
        val getCharacterUseCase = mockk<GetCharacterUseCase>()

        val viewModel = DetailViewModel(getCharacterUseCase, savedStateHandle)

        // Given
        val characterId = 1
        val errorMessage = "Error occurred"
        val flow = flowOf(CharacterResult.Error(errorMessage))
        coEvery { getCharacterUseCase(characterId) } returns flow

        // When
        viewModel.getCharacter()

        // Then
        assertFalse(viewModel.state.isLoading)
        assertNull(viewModel.state.character)
        coVerify { getCharacterUseCase(characterId) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test getCharacter loading`() = runTest {
        val savedStateHandle = mockk<SavedStateHandle>()
        every { savedStateHandle.get<Int>("id") } returns 1
        val viewModel = DetailViewModel(getCharacterUseCase, savedStateHandle)

        // Given
        val characterId = 1
        val flow = flowOf(CharacterResult.Loading(true))
        coEvery { getCharacterUseCase(characterId) } returns flow

        // When
        viewModel.getCharacter()

        // Then
        assertEquals(true, viewModel.state.isLoading)
        assertEquals(null, viewModel.state.character)
        coVerify { getCharacterUseCase(characterId) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test updateState`() = runTest{
        val savedStateHandle = mockk<SavedStateHandle>()
        every { savedStateHandle.get<Int>("id") } returns 1
        val viewModel = DetailViewModel(getCharacterUseCase, savedStateHandle)

        // Given
        val character = Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            gender = "Male",
            origin = Origin(name = "Earth (C-137)", url = "https://rickandmortyapi.com/api/location/1"),
            location = Location(name = "Earth (Replacement Dimension)", url = "https://rickandmortyapi.com/api/location/20"),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
        )

        // When
        viewModel.updateState(character, isLoading = false)

        // Then
        assertEquals(character, viewModel.state.character)
        assertEquals(false, viewModel.state.isLoading)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test handleError`() = runTest{
        val savedStateHandle = mockk<SavedStateHandle>()
        every { savedStateHandle.get<Int>("id") } returns 1
        val viewModel = DetailViewModel(getCharacterUseCase, savedStateHandle)

        // When
        viewModel.handleError(isError = true)

        // Then
        assertEquals(true, viewModel.state.isLoading)
        assertEquals(null, viewModel.state.character)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test showSnackbarMethod`() = runTest{
        val savedStateHandle = mockk<SavedStateHandle>()
        every { savedStateHandle.get<Int>("id") } returns 1
        val viewModel = DetailViewModel(getCharacterUseCase, savedStateHandle)

        // Given
        val message = "Test message"
        val eventFlowMock = mockk<MutableSharedFlow<DetailViewModel.UIEvent>>(relaxed = true)
        viewModel.fEventFlow = eventFlowMock

        // When
        viewModel.showSnackbarMethod(message)

        // Then
        coVerify { eventFlowMock.emit(DetailViewModel.UIEvent.ShowSnackbar(message)) }
    }
}