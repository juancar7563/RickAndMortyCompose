package com.example.rickandmortymvvm.domain.use_case

import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.domain.model.CharactersResultModel
import com.example.rickandmortymvvm.domain.repositories.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(page: Int): Flow<Result<CharactersResultModel>> {
        return repository.getCharacters(page)
    }
}