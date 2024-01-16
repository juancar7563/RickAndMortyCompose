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
        if (!commonRepository.getCharacters().value?.isEmpty()!!){
            listAlCharacters.addAll(commonRepository.getCharacters().value!!)
            currentPage = 2;
            state = state.copy(
                characters = listAlCharacters,
                isLoading = false,
                showPrevious = false,
                showNext = false
            )
        } else {
            getCharacters(increase = false)
        }
    }

    fun getCharacters(increase: Boolean) {
        viewModelScope.launch {
            if (maxCurrentPage != currentPage) {
                if (increase) currentPage++ else if (currentPage > 1) currentPage--
                val showPrevious = currentPage > 1
                getCharactersUseCase(currentPage).onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            val showNext = currentPage < result.data?.info?.pages!!
                            if (maxCurrentPage == 0) maxCurrentPage = result.data?.info?.pages!!
                            result.data?.characters?.toMutableList()
                                ?.let { listAlCharacters.addAll(it) }
                            delay(2000)
                            state = state.copy(
                                characters = listAlCharacters,
                                isLoading = false,
                                showPrevious = showPrevious,
                                showNext = showNext
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
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }
}