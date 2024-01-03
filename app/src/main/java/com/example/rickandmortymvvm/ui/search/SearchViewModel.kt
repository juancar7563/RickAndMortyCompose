package com.example.rickandmortymvvm.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.domain.use_case.GetCharacterSearchUseCase
import com.example.rickandmortymvvm.domain.use_case.GetCharactersMoreSearchCase
import com.example.rickandmortymvvm.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
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

    var state by mutableStateOf(SearchState(isLoading = false))
        private set

    private val fEventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = fEventFlow.asSharedFlow()

    private val listAlCharacters: MutableList<Characters> = mutableListOf()

    private var currentPage = 1
    private var maxPage = -1

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.EnteredCharacter -> state = state.copy(nameInput = event.value)
        }
    }

    fun getSearchCharacters(name: String) {
        if (name.length <= 2) {
            viewModelScope.launch {
                fEventFlow.emit(
                    UIEvent.ShowSnackbar(
                        UiText.StringResource(R.string.character_error, 2)
                    )
                )
            }
        } else {
            viewModelScope.launch {
                getCharacterSearchUseCase(name).onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            maxPage = result.data?.info?.pages!!
                            val showNext = currentPage < result.data?.info?.pages!!
                            if (showNext) currentPage++ else if (currentPage > 1) currentPage--
                            listAlCharacters.clear()
                            result.data.characters.toMutableList()
                                .let { listAlCharacters.addAll(it) }

                            state = state.copy(
                                characters = listAlCharacters,
                                isLoading = false,
                                nameInput = name
                            )
                        }

                        is Result.Error -> {
                            state = state.copy(
                                isLoading = false
                            )

                            fEventFlow.emit(
                                UIEvent.ShowSnackbar(
                                    UiText.StringResource(R.string.character_error, 2)
                                )
                            )
                        }

                        is Result.Loading -> {
                            var prueba = 0
                            state = state.copy(
                                isLoading = true
                            )
                        }
                    }
                }.launchIn(this)
            }
        }

    }

    fun getMoreSearchCharacters(name: String) {
        if (name.length <= 2) {
            viewModelScope.launch {
                fEventFlow.emit(
                    UIEvent.ShowSnackbar(
                        UiText.StringResource(R.string.character_error, 2)
                    )
                )
            }
        } else if (maxPage != -1 && currentPage < maxPage) {
            viewModelScope.launch {
                getCharactersMoreSearchCase(currentPage, name).onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            maxPage = result.data?.info?.pages!!
                            val showNext = currentPage < result.data?.info?.pages!!
                            if (showNext) currentPage++ else if (currentPage > 1) currentPage--
                            result.data.characters.toMutableList()
                                .let { listAlCharacters.addAll(it) }
                            state = state.copy(
                                characters = listAlCharacters,
                                isLoading = false,
                                nameInput = name
                            )
                        }

                        is Result.Error -> {
                            state = state.copy(
                                isLoading = false
                            )

                            fEventFlow.emit(
                                UIEvent.ShowSnackbar(
                                    UiText.StringResource(R.string.character_error, 3)
                                )
                            )
                        }

                        is Result.Loading -> {
                            state = state.copy(
                                isLoading = true
                            )
                        }
                    }
                }.launchIn(this)
            }
        }
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: UiText) : UIEvent()
    }
}