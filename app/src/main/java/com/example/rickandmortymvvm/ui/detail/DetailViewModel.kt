package com.example.rickandmortymvvm.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortymvvm.domain.use_case.GetCharacterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.ui.home.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getCharacterUseCase: GetCharacterUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    var state by mutableStateOf(DetailState(isLoading = true))
        private set
    private val fEventFlow = MutableSharedFlow<HomeViewModel.UIEvent>()

    init {
        getCharacter()
    }

    private fun getCharacter() {
        savedStateHandle.get<Int>("id")?.let { characterId ->
            viewModelScope.launch {
                getCharacterUseCase(characterId).onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            state = state.copy(
                                character = result.data,
                                isLoading = false
                            )
                        }
                        is Result.Error -> {
                            state = state.copy(
                                isLoading = false
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
}