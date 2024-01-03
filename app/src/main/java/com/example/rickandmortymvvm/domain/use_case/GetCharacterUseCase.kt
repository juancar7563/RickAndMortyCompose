package com.example.rickandmortymvvm.domain.use_case

import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.domain.model.Character
import com.example.rickandmortymvvm.domain.repositories.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharacterUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(id:Int): Flow<Result<Character>> {
        return repository.getCharacter(id)
    }
}