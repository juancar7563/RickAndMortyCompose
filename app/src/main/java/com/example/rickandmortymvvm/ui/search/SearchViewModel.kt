package com.example.rickandmortymvvm.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.domain.use_case.CharacterResultList
import com.example.rickandmortymvvm.domain.use_case.GetCharacterSearchUseCase
import com.example.rickandmortymvvm.domain.use_case.GetCharactersMoreSearchCase
import com.example.rickandmortymvvm.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getCharacterSearchUseCase: GetCharacterSearchUseCase,
    private val getCharactersMoreSearchCase: GetCharactersMoreSearchCase
) : ViewModel() {


    private val originState = MutableStateFlow(SearchState())
    var state: StateFlow<SearchState> = originState
    var fEventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = fEventFlow.asSharedFlow()

    val listAlCharacters: MutableList<Characters> = mutableListOf()

    var currentPage = 1
    var maxPage = -1
    private val regexPattern = "^[a-zA-Z0-9-]*$".toRegex()

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.EnteredCharacter -> originState.value = originState.value.copy(nameInput = event.value)
        }
    }

    fun showLoading(isLoading: Boolean) {
        originState.value = originState.value.copy(isLoading = isLoading)
    }

    fun getSearchCharacters(name: String) {
        if (!isValidName(name)) {
            viewModelScope.launch {
                showSnackbarMethod(UiText.StringResource(R.string.character_error, 3))

            }
        } else {
            showLoading(true)
            viewModelScope.launch {
                getCharacterSearchUseCase(name).onEach { result ->
                    when (result) {
                        is CharacterResultList.Success -> {
                            updatePage(result.character?.info?.pages!!)
                            updateCurrentPage(currentPage < result.character?.info?.pages!!)
                            updateState(
                                result.character.characters.toMutableList(),
                                false,
                                name,
                                true
                            )
                        }

                        is CharacterResultList.Error -> {
                            handleError(true)
                            showSnackbarMethod(UiText.StringResource(R.string.character_error, 3))
                        }

                        is CharacterResultList.Loading -> {
                            showLoading(true)
                        }
                    }
                }.launchIn(this)
            }
        }

    }

    fun getMoreSearchCharacters(name: String) {
        if (!isValidName(name)) {
            viewModelScope.launch {
                showSnackbarMethod(UiText.StringResource(R.string.character_error, 3))
            }
        } else if (maxPage != -1 && currentPage < maxPage) {
            showLoading(true)
            viewModelScope.launch {
                getCharactersMoreSearchCase(currentPage, name).onEach { result ->
                    when (result) {
                        is CharacterResultList.Success -> {
                            updatePage(result.character?.info?.pages!!)
                            updateCurrentPage(true)
                            updateState(
                                result.character.characters.toMutableList(),
                                false,
                                name,
                                false
                            )
                        }

                        is CharacterResultList.Error -> {
                            handleError(true)
                            showSnackbarMethod(UiText.StringResource(R.string.character_error, 3))
                        }

                        is CharacterResultList.Loading -> {
                            showLoading(true)
                        }
                    }
                }.launchIn(this)
            }
        }
    }

    fun updateState(
        characters: MutableList<Characters>,
        isLoading: Boolean,
        name: String,
        isFirstTime: Boolean
    ) {
        if (isFirstTime) {
            listAlCharacters.clear()
        }
        listAlCharacters.addAll(characters)

        originState.value = originState.value.copy(
            characters = listAlCharacters,
            isLoading = isLoading,
            nameInput = name
        )
    }

    fun handleError(isError: Boolean) {
        originState.value = originState.value.copy(isLoading = isError)
    }

    fun isValidName(name: String): Boolean {
        return name.length > 2 && name.matches(regexPattern)
    }

    fun updatePage(maxPages: Int): Boolean {
        maxPage = maxPages
        return currentPage < maxPages
    }

    fun updateCurrentPage(showNext: Boolean) {
        if (showNext) currentPage++ else if (currentPage > 1) currentPage--
    }

    suspend fun showSnackbarMethod(message: UiText.StringResource) {
        fEventFlow.emit(UIEvent.ShowSnackbar(message))
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: UiText) : UIEvent()
    }
}