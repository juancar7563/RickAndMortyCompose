package com.example.rickandmortymvvm.domain.use_case

import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.domain.repositories.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val MIN_PAGE = 2

class GetCharactersMoreSearchCase @Inject constructor(
    private val repository: CharacterRepository
) {
    val errorMessage = "Error occurred"
    val flowWithError = flow<CharacterResultList> {
        throw IllegalStateException(errorMessage)
    }

    operator fun invoke(page:Int, name: String): Flow<CharacterResultList> {
        if(page >= MIN_PAGE) {
            return repository.getFilterMoreCharacters(page, name).map { result ->
                when (result) {
                    is Result.Success -> CharacterResultList.Success(result.data!!)
                    is Result.Error -> CharacterResultList.Error(result.message ?: "Unknown error")
                    is Result.Loading -> CharacterResultList.Loading(true)
                }
            }
        }
        return flowWithError
    }
}