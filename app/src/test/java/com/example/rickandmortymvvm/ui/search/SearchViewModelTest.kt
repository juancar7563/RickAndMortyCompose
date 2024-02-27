package com.example.rickandmortymvvm.ui.search


import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.domain.use_case.GetCharacterSearchUseCase
import com.example.rickandmortymvvm.domain.use_case.GetCharactersMoreSearchCase
import com.example.rickandmortymvvm.util.UiText
import org.junit.Before
import org.junit.Test
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import com.example.rickandmortymvvm.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest

class SearchViewModelTest {

    @Mock
    lateinit var fEventFlow: Flow<SearchViewModel.UIEvent>
    private lateinit var getCharacterSearchUseCase: GetCharacterSearchUseCase
    private lateinit var getCharactersMoreSearchCase: GetCharactersMoreSearchCase
    @Before
    fun setup() {
        getCharacterSearchUseCase = mockk()
        getCharactersMoreSearchCase = mockk()
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `updateState should update state correctly and first time`() {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        val character1 = Characters(1, "Pepe", "Vallecano", "qweqwewq")
        val character2 = Characters(2, "Juja", "Coloso", "popopop")
        val character3 = Characters(3, "First", "Coco", "quiqui")
        val charactersPrevious = mutableListOf(character3)
        val characters = mutableListOf(character1, character2)

        viewModel.listAlCharacters.addAll(charactersPrevious)
        // Act
        viewModel.updateState(characters, isLoading = true, name = "Test", isFirstTime = true)

        // Assert
        assertEquals(characters, viewModel.state.characters)
        assertTrue(viewModel.state.isLoading)
        assertEquals("Test", viewModel.state.nameInput)

        assertTrue(!viewModel.state.characters.containsAll(charactersPrevious))
    }

    @Test
    fun `updateState should update state correctly but no first time`() {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        val character1 = Characters(1, "Pepe", "Vallecano", "qweqwewq")
        val character2 = Characters(2, "Juja", "Coloso", "popopop")
        val character3 = Characters(3, "First", "Coco", "quiqui")
        val charactersPrevious = mutableListOf(character3)
        val characters = mutableListOf(character1, character2)

        viewModel.listAlCharacters.addAll(charactersPrevious)
        // Act
        viewModel.updateState(characters, isLoading = true, name = "Test", isFirstTime = false)
        charactersPrevious.addAll(characters)
        // Assert
        assertEquals(charactersPrevious, viewModel.state.characters)
        assertTrue(viewModel.state.isLoading)
        assertEquals("Test", viewModel.state.nameInput)

        assertTrue(viewModel.state.characters.containsAll(charactersPrevious))
    }

    @Test
    fun `handleError should update isLoading correctly when isError is true`() = runTest {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)

        // Act
        viewModel.handleError(isError = true)

        // Assert
        assertTrue(viewModel.state.isLoading)
    }

    @Test
    fun `handleError should update isLoading correctly when isError is false`() = runTest {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        viewModel.state = viewModel.state.copy(isLoading = true)

        // Act
        viewModel.handleError(isError = false)

        // Assert
        assertFalse(viewModel.state.isLoading)
    }

    @Test
    fun `isValidName should return true for valid names`() {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        val validNames = listOf("John", "Doe", "JaneDoe123")

        // Act & Assert
        validNames.forEach {
            assertTrue(viewModel.isValidName(it))
        }
    }

    @Test
    fun `isValidName should return false for invalid names`() {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        val invalidNames = listOf("", "A", "12")

        // Act & Assert
        invalidNames.forEach {
            assertFalse(viewModel.isValidName(it))
        }
    }

    @Test
    fun `updatePage should update maxPage and return true if currentPage is less than maxPages`() {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        val maxPages = 10

        // Act
        val result = viewModel.updatePage(maxPages)

        // Assert
        assertEquals(maxPages, viewModel.maxPage)
        assertTrue(result)
    }

    @Test
    fun `updatePage should update maxPage and return false if currentPage is greater than or equal to maxPages`() {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        viewModel.currentPage = 10
        val maxPages = 10

        // Act
        val result = viewModel.updatePage(maxPages)

        // Assert
        assertEquals(maxPages, viewModel.maxPage)
        assertFalse(result)
    }

    @Test
    fun `updateCurrentPage should increment currentPage if showNext is true`() {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        viewModel.currentPage = 1

        // Act
        viewModel.updateCurrentPage(showNext = true)

        // Assert
        assertEquals(2, viewModel.currentPage)
    }

    @Test
    fun `updateCurrentPage should decrement currentPage if showNext is false and currentPage is greater than 1`() {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        viewModel.currentPage = 3

        // Act
        viewModel.updateCurrentPage(showNext = false)

        // Assert
        assertEquals(2, viewModel.currentPage)
    }

    @Test
    fun `updateCurrentPage should not decrement currentPage if showNext is false and currentPage is 1`() {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        viewModel.currentPage = 1

        // Act
        viewModel.updateCurrentPage(showNext = false)

        // Assert
        assertEquals(1, viewModel.currentPage)
    }

    @Test
    fun `showSnackbarMethod should emit UIEvent ShowSnackbar with correct message using mockk`() = runTest {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        val message = UiText.StringResource(R.string.character_error)
        val eventFlowMock = mockk<MutableSharedFlow<SearchViewModel.UIEvent>>(relaxed = true)
        viewModel.fEventFlow = eventFlowMock

        // Act
        viewModel.showSnackbarMethod(message)

        // Assert
        coVerify { eventFlowMock.emit(SearchViewModel.UIEvent.ShowSnackbar(message)) }
    }
}