package com.example.rickandmortymvvm.ui.search

import com.example.rickandmortymvvm.domain.model.Characters

data class SearchState (
    val characters: MutableList<Characters> = mutableListOf(),
    val isLoading: Boolean = false,
    val nameInput: String = ""
)