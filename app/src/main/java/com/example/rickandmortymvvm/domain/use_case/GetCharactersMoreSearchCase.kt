package com.example.rickandmortymvvm.domain.use_case

import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.domain.model.CharactersResultModel
import com.example.rickandmortymvvm.domain.repositories.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

const val MIN_PAGE = 2

class GetCharactersMoreSearchCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(page:Int, name: String): Flow<Result<CharactersResultModel>> {
        if(page >= MIN_PAGE) {
            return repository.getFilterMoreCharacters(page, name)
        }
        return TODO("Provide the return value")
    }
}