package com.example.rickandmortymvvm.domain.use_case

import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.domain.model.CharactersResultModel
import com.example.rickandmortymvvm.domain.repositories.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(page: Int): Flow<CharacterResultList> {
        return repository.getCharacters(page).map { result ->
            when (result) {
                is Result.Success -> CharacterResultList.Success(result.data!!)
                is Result.Error -> CharacterResultList.Error(result.message ?: "Unknown error")
                is Result.Loading -> CharacterResultList.Loading(true)
            }
        }
    }
}