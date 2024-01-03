package com.example.rickandmortymvvm.ui.detail

import com.example.rickandmortymvvm.domain.model.Character

data class DetailState(
    val character: Character? = null,
    val isLoading: Boolean = false
)