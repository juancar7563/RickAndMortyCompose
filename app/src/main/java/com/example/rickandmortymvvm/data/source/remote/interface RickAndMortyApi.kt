package com.example.rickandmortymvvm.data.source.remote

import com.example.rickandmortymvvm.data.source.remote.dto.CharacterDto
import com.example.rickandmortymvvm.data.source.remote.dto.CharactersDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val CHARACTERS_ENDPOINT = "character/"
const val CHARACTER_ENDPOINT = "character/{id}"
const val PAGE_PARAMETER = "page"
const val NAME_PARAMETER = "name"

interface RickAndMortyApi {

    @GET(CHARACTERS_ENDPOINT)
    suspend fun getCharacters(
        @Query("page") page: Int
    ): CharactersDto

    @GET(CHARACTER_ENDPOINT)
    suspend fun getCharacter(
        @Path("id") id: Int
    ): CharacterDto

    @GET(CHARACTERS_ENDPOINT)
    suspend fun getFilterCharacters(
        @Query(NAME_PARAMETER) name: String
    ): CharactersDto

    @GET(CHARACTERS_ENDPOINT)
    suspend fun getFilterMoreCharacters(
        @Query(PAGE_PARAMETER) page: Int,
        @Query(NAME_PARAMETER) name: String
    ): CharactersDto
}