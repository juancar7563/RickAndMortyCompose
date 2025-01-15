package com.example.rickandmortymvvm.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import com.example.rickandmortymvvm.domain.use_case.GetCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.data.repositories.CommonRepository
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.domain.use_case.CharacterResultList
import com.example.rickandmortymvvm.ui.login.LoginState
import com.google.firebase.auth.FirebaseAuth
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

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mutableAuthState = MutableLiveData<LoginState>()
    var state by mutableStateOf(HomeState(isLoading = true))
        set

    var fEventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = fEventFlow.asSharedFlow()

    val listAlCharacters: MutableList<Characters> = mutableListOf()

    var currentPage = 1
    var maxCurrentPage = 0

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
                        is CharacterResultList.Success -> {
                            updateCurrentPage(currentPage < result.character?.info?.pages!!)
                            if (maxCurrentPage == 0) updatePage(result.character?.info?.pages!!)
                            delay(2000)
                            updateState(
                                result.character?.characters?.toMutableList(),
                                false,
                                showPrevious,
                                false
                            )
                        }

                        is CharacterResultList.Error -> {
                            handleError(false)
                            showSnackbarMethod(result.message ?: "Unknown error")
                        }

                        is CharacterResultList.Loading -> {
                            handleError(true)
                        }
                    }
                }.launchIn(this)
            }
        }
    }

    fun getCharactersFromSplash() {
        val characters = commonRepository.getCharacters()
        if (!characters.isNullOrEmpty()) {
            listAlCharacters.clear()
            currentPage = 2
            updateState(characters, false, false, false)
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

    fun signout() {
        auth.signOut()
        mutableAuthState.value = LoginState.Unauthenticated
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }
}