package com.example.rickandmortymvvm.ui.search


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
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
import com.example.rickandmortymvvm.domain.model.CharactersResultModel
import com.example.rickandmortymvvm.domain.model.InfoModel
import com.example.rickandmortymvvm.domain.use_case.CharacterResultList
import com.example.rickandmortymvvm.ui.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class SearchViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getCharacterSearchUseCase: GetCharacterSearchUseCase
    private lateinit var getCharactersMoreSearchCase: GetCharactersMoreSearchCase
    private lateinit var savedStateHandle: SavedStateHandle


    @Before
    fun setup() {
        getCharacterSearchUseCase = mockk()
        getCharactersMoreSearchCase = mockk()
        savedStateHandle = mockk()
    }

    @Test
    fun `getMoreSearchCharacters - invalid name`() = runTest {

        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)

        val invalidName = "123"
        every { savedStateHandle.get<Int>("maxPage") } returns 10
        every { savedStateHandle.get<Int>("currentPage") } returns 5

        viewModel.getMoreSearchCharacters(invalidName)

        viewModel.showSnackbarMethod(UiText.StringResource(R.string.character_error, 3))
    }

    @Test
    fun `getMoreSearchCharacters - valid name and maxPage reached`() = runTest {

        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)

        val validName = "Valid Name"
        val maxPage = 5
        every { savedStateHandle.get<Int>("maxPage") } returns maxPage
        every { savedStateHandle.get<Int>("currentPage") } returns maxPage

        viewModel.getMoreSearchCharacters(validName)


        verify(exactly = 0) { getCharactersMoreSearchCase(any(), any()) }
    }

    @Test
    fun `getMoreSearchCharacters - valid name and not maxPage reached`() = runTest {

        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)

        val validName = "Pepe"
        val maxPage = 10
        val currentPage = 5

        val infoModel = InfoModel(4, "false", 4, "false")
        val characters = mutableListOf(
            Characters(1, "Pepe", "Vallecano", "qweqwewq"),
            Characters(2, "Juja", "Coloso", "popopop")
        )
        viewModel.maxPage = maxPage
        viewModel.currentPage = currentPage

        val charactersResultModel = CharactersResultModel(infoModel, characters)
        val flow = flowOf(CharacterResultList.Success(charactersResultModel))
        coEvery { getCharactersMoreSearchCase(currentPage, validName) } returns flow

        viewModel.getMoreSearchCharacters(validName)

        coVerify { getCharactersMoreSearchCase(currentPage, validName) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `updateState should update state correctly and first time`() = runTest {
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

    @ExperimentalCoroutinesApi
    @Test
    fun `updateState should update state correctly but no first time`() = runTest {
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

    @ExperimentalCoroutinesApi
    @Test
    fun `handleError should update isLoading correctly when isError is true`() = runTest {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)

        // Act
        viewModel.handleError(isError = true)

        // Assert
        assertTrue(viewModel.state.isLoading)
    }

    @ExperimentalCoroutinesApi
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

    @ExperimentalCoroutinesApi
    @Test
    fun `isValidName should return true for valid names`() = runTest {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        val validNames = listOf("John", "Doe", "JaneDoe123")

        // Act & Assert
        validNames.forEach {
            assertTrue(viewModel.isValidName(it))
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `isValidName should return false for invalid names`() = runTest {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        val invalidNames = listOf("", "A", "12")

        // Act & Assert
        invalidNames.forEach {
            assertFalse(viewModel.isValidName(it))
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `updatePage should update maxPage and return true if currentPage is less than maxPages`() = runTest {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        val maxPages = 10

        // Act
        val result = viewModel.updatePage(maxPages)

        // Assert
        assertEquals(maxPages, viewModel.maxPage)
        assertTrue(result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `updatePage should update maxPage and return false if currentPage is greater than or equal to maxPages`() = runTest {
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

    @ExperimentalCoroutinesApi
    @Test
    fun `updateCurrentPage should increment currentPage if showNext is true`() = runTest {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        viewModel.currentPage = 1

        // Act
        viewModel.updateCurrentPage(showNext = true)

        // Assert
        assertEquals(2, viewModel.currentPage)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `updateCurrentPage should decrement currentPage if showNext is false and currentPage is greater than 1`() = runTest {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        viewModel.currentPage = 3

        // Act
        viewModel.updateCurrentPage(showNext = false)

        // Assert
        assertEquals(2, viewModel.currentPage)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `updateCurrentPage should not decrement currentPage if showNext is false and currentPage is 1`() = runTest {
        // Arrange
        val viewModel = SearchViewModel(getCharacterSearchUseCase, getCharactersMoreSearchCase)
        viewModel.currentPage = 1

        // Act
        viewModel.updateCurrentPage(showNext = false)

        // Assert
        assertEquals(1, viewModel.currentPage)
    }

    @ExperimentalCoroutinesApi
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