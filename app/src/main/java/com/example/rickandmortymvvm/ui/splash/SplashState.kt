package com.example.rickandmortymvvm.ui.splash

import com.example.rickandmortymvvm.domain.model.Characters

data class SplashState(
    val characters: MutableList<Characters> = mutableListOf(),
    val isLoading: Boolean = false
)