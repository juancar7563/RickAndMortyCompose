package com.example.rickandmortymvvm.data.repositories

import com.example.rickandmortymvvm.data.Result
import com.example.rickandmortymvvm.data.source.remote.RickAndMortyApi
import com.example.rickandmortymvvm.data.source.remote.dto.toCharacter
import com.example.rickandmortymvvm.data.source.remote.dto.toListCharacters
import com.example.rickandmortymvvm.domain.model.Character
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.domain.model.CharactersResultModel
import com.example.rickandmortymvvm.domain.repositories.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi
): CharacterRepository {
    override fun getCharacters(page: Int): Flow<Result<CharactersResultModel>> = flow{
        emit(Result.Loading())
        try {
            val response = api.getCharacters(page).toListCharacters()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(
                message = "Algo ha ido mal",
                data = null
            ))
        } catch (e: IOException){
            emit(Result.Error(
                message = "No se pudo conectar",
                data = null
            ))
        }
    }

    override fun getCharacter(id: Int): Flow<Result<Character>> = flow{
        emit(Result.Loading())
        try {
            val response = api.getCharacter(id).toCharacter()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(
                message = "Algo ha ido mal",
                data = null
            ))
        } catch (e: IOException){
            emit(Result.Error(
                message = "No se pudo conectar",
                data = null
            ))
        }
    }

    override fun getFilterCharacters(name: String): Flow<Result<CharactersResultModel>> = flow{
        emit(Result.Loading())
        try {
            val response = api.getFilterCharacters(name).toListCharacters()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(
                message = "Algo ha ido mal",
                data = null
            ))
        } catch (e: IOException){
            emit(Result.Error(
                message = "No se pudo conectar",
                data = null
            ))
        }
    }

    override fun getFilterMoreCharacters(page: Int, name: String): Flow<Result<CharactersResultModel>> = flow{
        emit(Result.Loading())
        try {
            val response = api.getFilterMoreCharacters(page, name).toListCharacters()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(
                message = "Algo ha ido mal",
                data = null
            ))
        } catch (e: IOException){
            emit(Result.Error(
                message = "No se pudo conectar",
                data = null
            ))
        }
    }
}