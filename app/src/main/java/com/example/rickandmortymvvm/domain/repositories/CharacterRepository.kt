package com.example.rickandmortymvvm.domain.repositories

import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.domain.model.Character
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.domain.model.CharactersResultModel
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    fun getCharacters(page: Int): Flow<Result<CharactersResultModel>>

    fun getCharacter(id: Int): Flow<Result<Character>>

    fun getFilterCharacters(name: String): Flow<Result<CharactersResultModel>>

    fun getFilterMoreCharacters(page: Int, name: String): Flow<Result<CharactersResultModel>>
}