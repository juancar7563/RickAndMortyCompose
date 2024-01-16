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
                        result.data?.characters?.toMutableList()
                            ?.let { listAlCharacters.addAll(it) }

                        commonRepository.setCharacters(listAlCharacters)

                        state = state.copy(
                            characters = listAlCharacters,
                            isLoading = false,
                        )
                    }

                    is Result.Error -> {
                        state = state.copy(
                            isLoading = false
                        )

                        fEventFlow.emit(
                            UIEvent.ShowSnackbar(
                                result.message ?: "Unknown error"
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

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }
}