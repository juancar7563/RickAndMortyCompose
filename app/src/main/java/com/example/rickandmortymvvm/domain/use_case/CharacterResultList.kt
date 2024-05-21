package com.example.rickandmortymvvm.domain.use_case

import com.example.rickandmortymvvm.domain.model.CharactersResultModel

sealed class CharacterResultList {
    data class Success(val character: CharactersResultModel) : CharacterResultList()
    data class Error(val message: String) : CharacterResultList()
    data class Loading(val loading: Boolean) : CharacterResultList()
}