package com.example.rickandmortymvvm.domain.use_case

import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.domain.model.Character
import com.example.rickandmortymvvm.domain.repositories.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

sealed class CharacterResult {
    data class Success(val character: Character) : CharacterResult()
    data class Error(val message: String) : CharacterResult()
    data class Loading(val loading: Boolean) : CharacterResult()
}

class GetCharacterUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(id: Int): Flow<CharacterResult> {
        return repository.getCharacter(id).map { result ->
            when (result) {
                is Result.Success -> CharacterResult.Success(result.data!!)
                is Result.Error -> CharacterResult.Error(result.message ?: "Unknown error")
                is Result.Loading -> CharacterResult.Loading(true)
            }
        }
    }
}