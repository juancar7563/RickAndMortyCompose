package com.example.rickandmortymvvm.ui.home

import com.example.rickandmortymvvm.domain.model.Characters

data class HomeState (
    val characters: MutableList<Characters> = mutableListOf(),
    val showPrevious: Boolean = false,
    val showNext:Boolean = false,
    val isLoading: Boolean = false
)