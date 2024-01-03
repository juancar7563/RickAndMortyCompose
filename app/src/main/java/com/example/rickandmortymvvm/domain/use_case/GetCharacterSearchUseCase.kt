package com.example.rickandmortymvvm.domain.use_case

import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.domain.model.CharactersResultModel
import com.example.rickandmortymvvm.domain.repositories.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharacterSearchUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(name: String): Flow<Result<CharactersResultModel>> {
        return repository.getFilterCharacters(name)
    }
}