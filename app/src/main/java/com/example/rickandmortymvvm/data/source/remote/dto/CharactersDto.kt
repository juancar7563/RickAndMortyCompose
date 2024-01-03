package com.example.rickandmortymvvm.data.source.remote.dto

import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.domain.model.CharactersResultModel
import com.example.rickandmortymvvm.domain.model.InfoModel

data class CharactersDto(
    val info: Info,
    val results: List<Result>
)

fun CharactersDto.toListCharacters(): CharactersResultModel {
    val resultEntries = results.mapIndexed { _, entries ->
        Characters(
            id = entries.id,
            name = entries.name,
            specie = entries.species,
            image = entries.image,
        )
    }

    val resultInfo =
        InfoModel(
            count = info.count ?: 0,
            next = info.next ?: "",
            pages = info.pages ?: 0,
            prev = info.prev ?: "",
        )

    return CharactersResultModel(resultInfo, resultEntries)
}

fun CharactersDto.getInfo(): Info {
    return info
}