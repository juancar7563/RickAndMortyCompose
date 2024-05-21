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
import com.example.rickandmortymvvm.domain.model.Character
import com.example.rickandmortymvvm.domain.use_case.CharacterResult
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
    var fEventFlow = MutableSharedFlow<UIEvent>()

    init {
        getCharacter()
    }

    fun getCharacter() {
        savedStateHandle.get<Int>("id")?.let { characterId ->
            viewModelScope.launch {
                getCharacterUseCase(characterId).onEach { result ->
                    when (result) {
                        is CharacterResult.Success -> {
                            updateState(result.character, isLoading = false)
                        }
                        is CharacterResult.Error -> {
                            handleError(false)
                            showSnackbarMethod(result.message ?: "Unknown error")
                        }
                        is CharacterResult.Loading -> {
                            handleError(true)
                        }
                    }
                }.launchIn(this)
            }
        }
    }

    fun updateState(character: Character?, isLoading: Boolean) {
        state = state.copy(
            character = character,
            isLoading = isLoading,
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