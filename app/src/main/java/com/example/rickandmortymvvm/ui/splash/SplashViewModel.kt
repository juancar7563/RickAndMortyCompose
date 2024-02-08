package com.example.rickandmortymvvm.ui.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.data.repositories.CommonRepository
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.domain.use_case.GetCharactersUseCase
import com.example.rickandmortymvvm.ui.home.HomeState
import com.example.rickandmortymvvm.ui.home.HomeViewModel
import com.example.rickandmortymvvm.ui.search.SearchViewModel
import com.example.rickandmortymvvm.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val commonRepository: CommonRepository
) : ViewModel() {

    var state by mutableStateOf(SplashState(isLoading = true))
        private set

    private val fEventFlow = MutableSharedFlow<UIEvent>()
    private val listAlCharacters: MutableList<Characters> = mutableListOf()
    private var currentPage = 1

    init {
        getCharacters()
    }

    fun getCharacters() {
        viewModelScope.launch {

            getCharactersUseCase(currentPage).onEach { result ->
                when (result) {
                    is Result.Success -> {
                        commonRepository.setCharacters(result.data?.characters?.toMutableList())
                        updateState(result.data?.characters?.toMutableList(), false)
                    }

                    is Result.Error -> {
                        handleError(false)
                        showSnackbarMethod(result.message ?: "Unknown error")
                    }

                    is Result.Loading -> {
                        handleError(true)
                    }
                }
            }.launchIn(this)

        }
    }

    fun updateState(characters: MutableList<Characters>?, isLoading: Boolean) {
        if (characters != null) {
            listAlCharacters.addAll(characters)
        }

        state = state.copy(
            characters = listAlCharacters,
            isLoading = isLoading
        )
    }

    fun handleError(isError: Boolean) {
        state = state.copy(isLoading = isError)
    }

    suspend fun showSnackbarMethod(message: String) {
        fEventFlow.emit(UIEvent.ShowSnackbar(message))
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }
}