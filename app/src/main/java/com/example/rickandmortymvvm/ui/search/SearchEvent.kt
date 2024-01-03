package com.example.rickandmortymvvm.ui.search

sealed class SearchEvent {
    data class EnteredCharacter(val value: String): SearchEvent()
}