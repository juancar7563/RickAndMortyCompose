package com.example.rickandmortymvvm.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.rickandmortymvvm.data.repositories.CommonRepository
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.domain.model.CharactersResultModel
import com.example.rickandmortymvvm.domain.use_case.CharacterResultList
import com.example.rickandmortymvvm.domain.use_case.GetCharactersUseCase
import com.example.rickandmortymvvm.ui.MainCoroutineRule
import com.nhaarman.mockitokotlin2.whenever
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule


@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var getCharactersUseCase: GetCharactersUseCase
    private lateinit var commonRepository: CommonRepository

    @Before
    fun setup() {
        getCharactersUseCase = mockk(relaxed = true)
        commonRepository = mockk(relaxed = true)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetCharactersFromSplash_RepositoryNotEmpty() = runTest {
        // Prepare test data
        val character1 = Characters(1, "Pepe", "Vallecano", "qweqwewq")
        val characters = mutableListOf(character1)

        // Mock the repository method
        every { commonRepository.getCharacters() } returns characters

        // Create the ViewModel after configuring the mock
        val viewModel = HomeViewModel(getCharactersUseCase, commonRepository)

        // Call the method
        viewModel.getCharactersFromSplash()

        // Verify that the list of characters is added and currentPage is set to 2
        assertEquals(characters, viewModel.listAlCharacters)
        assertEquals(2, viewModel.currentPage)

        // Verify interactions with the mocks
        verify { commonRepository.getCharacters() }
        // Verify that updateState was called with the correct parameters
        assertEquals(HomeState(characters, false, false, false), viewModel.state)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `updateState should update state correctly and first time`() = runTest {
        val viewModel = HomeViewModel(getCharactersUseCase, commonRepository)
        // Arrange
        val character1 = Characters(1, "Pepe", "Vallecano", "qweqwewq")
        val character2 = Characters(2, "Juja", "Coloso", "popopop")
        val character3 = Characters(3, "First", "Coco", "quiqui")
        val charactersPrevious = mutableListOf(character3)
        val characters = mutableListOf(character1, character2)

        viewModel.listAlCharacters.addAll(charactersPrevious)
        // Act
        viewModel.updateState(characters, isLoading = true, showPrevious = false, showNext = false)

        // Assert
        assertEquals(viewModel.listAlCharacters, viewModel.state.characters)
        assertTrue(viewModel.state.isLoading)

        assertTrue(viewModel.state.characters.containsAll(charactersPrevious))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `updateState should update state correctly but no first time`() = runTest {
        val viewModel = HomeViewModel(getCharactersUseCase, commonRepository)

        // Arrange
        val character1 = Characters(1, "Pepe", "Vallecano", "qweqwewq")
        val character2 = Characters(2, "Juja", "Coloso", "popopop")
        val character3 = Characters(3, "First", "Coco", "quiqui")
        val charactersPrevious = mutableListOf(character3)
        val characters = mutableListOf(character1, character2)

        viewModel.listAlCharacters.addAll(charactersPrevious)
        // Act
        viewModel.updateState(characters, isLoading = true, showPrevious = false, showNext = false)
        charactersPrevious.addAll(characters)
        // Assert
        assertEquals(charactersPrevious, viewModel.state.characters)
        assertTrue(viewModel.state.isLoading)

        assertTrue(viewModel.state.characters.containsAll(charactersPrevious))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `handleError should update isLoading correctly when isError is true`() = runTest {
        val viewModel = HomeViewModel(getCharactersUseCase, commonRepository)
        // Act
        viewModel.handleError(isError = true)

        // Assert
        assertTrue(viewModel.state.isLoading)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `handleError should update isLoading correctly when isError is false`() = runTest {
        val viewModel = HomeViewModel(getCharactersUseCase, commonRepository)
        // Arrange
        viewModel.state = viewModel.state.copy(isLoading = true)

        // Act
        viewModel.handleError(isError = false)

        // Assert
        assertFalse(viewModel.state.isLoading)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `updatePage should update maxPage and return true if currentPage is less than maxPages`() = runTest {
        val viewModel = HomeViewModel(getCharactersUseCase, commonRepository)
        // Arrange
        val maxPages = 10

        // Act
        val result = viewModel.updatePage(maxPages)

        // Assert
        assertEquals(maxPages, viewModel.maxCurrentPage)
        assertTrue(result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `updatePage should update maxPage and return false if currentPage is greater than or equal to maxPages`() = runTest {
        val viewModel = HomeViewModel(getCharactersUseCase, commonRepository)
        // Arrange
        viewModel.currentPage = 10
        val maxPages = 10

        // Act
        val result = viewModel.updatePage(maxPages)

        // Assert
        assertEquals(maxPages, viewModel.maxCurrentPage)
        assertFalse(result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `updateCurrentPage should increment currentPage if showNext is true`() = runTest {
        val viewModel = HomeViewModel(getCharactersUseCase, commonRepository)
        // Arrange
        viewModel.currentPage = 1

        // Act
        viewModel.updateCurrentPage(showNext = true)

        // Assert
        assertEquals(2, viewModel.currentPage)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `updateCurrentPage should decrement currentPage if showNext is false and currentPage is greater than 1`() = runTest {
        val viewModel = HomeViewModel(getCharactersUseCase, commonRepository)
        // Arrange
        viewModel.currentPage = 3

        // Act
        viewModel.updateCurrentPage(showNext = false)

        // Assert
        assertEquals(2, viewModel.currentPage)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `updateCurrentPage should not decrement currentPage if showNext is false and currentPage is 1`() = runTest {
        val viewModel = HomeViewModel(getCharactersUseCase, commonRepository)
        // Arrange
        viewModel.currentPage = 1

        // Act
        viewModel.updateCurrentPage(showNext = false)

        // Assert
        assertEquals(1, viewModel.currentPage)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `showSnackbarMethod should emit UIEvent ShowSnackbar with correct message using mockk`() = runTest {
        val viewModel = HomeViewModel(getCharactersUseCase, commonRepository)
        // Arrange
        val message = "Unknown error"
        val eventFlowMock = mockk<MutableSharedFlow<HomeViewModel.UIEvent>>(relaxed = true)
        viewModel.fEventFlow = eventFlowMock

        // Act
        viewModel.showSnackbarMethod(message)

        // Assert
        coVerify { eventFlowMock.emit(HomeViewModel.UIEvent.ShowSnackbar(message)) }
    }
}