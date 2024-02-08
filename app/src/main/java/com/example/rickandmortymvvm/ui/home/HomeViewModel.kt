package com.example.rickandmortymvvm.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.rickandmortymvvm.domain.use_case.GetCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.data.repositories.CommonRepository
import com.example.rickandmortymvvm.domain.model.Characters
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val commonRepository: CommonRepository
) : ViewModel() {

    var state by mutableStateOf(HomeState(isLoading = true))
        private set

    private val fEventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = fEventFlow.asSharedFlow()

    private val listAlCharacters: MutableList<Characters> = mutableListOf()

    private var currentPage = 1
    private var maxCurrentPage = 0

    init {
        getCharactersFromSplash()
    }

    fun getCharacters(increase: Boolean) {
        viewModelScope.launch {
            if (maxCurrentPage != currentPage) {
                if (increase) currentPage++ else if (currentPage > 1) currentPage--
                val showPrevious = currentPage > 1
                getCharactersUseCase(currentPage).onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            updateCurrentPage(currentPage < result.data?.info?.pages!!)
                            if (maxCurrentPage == 0) updatePage(result.data?.info?.pages!!)
                            delay(2000)
                            updateState(
                                result.data?.characters?.toMutableList(),
                                false,
                                showPrevious,
                                false
                            )
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
    }

    fun getCharactersFromSplash() {
        if (!commonRepository.getCharacters().value?.isEmpty()!!) {
            listAlCharacters.addAll(commonRepository.getCharacters().value!!)
            currentPage = 2;
            updateState(listAlCharacters, false, false, false)
        } else {
            getCharacters(increase = false)
        }
    }

    fun updateState(characters: MutableList<Characters>?, isLoading: Boolean, showPrevious: Boolean, showNext: Boolean) {
        if (characters != null) {
            listAlCharacters.addAll(characters)
        }
        state = state.copy(
            characters = listAlCharacters,
            isLoading = isLoading,
            showPrevious = showPrevious,
            showNext = showNext
        )
    }

    fun handleError(isError: Boolean) {
        state = state.copy(isLoading = isError)
    }

    suspend fun showSnackbarMethod(message: String) {
        fEventFlow.emit(UIEvent.ShowSnackbar(message))
    }

    fun updatePage(maxPages: Int): Boolean {
        maxCurrentPage = maxPages
        return currentPage < maxPages
    }

    fun updateCurrentPage(showNext: Boolean) {
        if (showNext) currentPage++ else if (currentPage > 1) currentPage--
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }
}