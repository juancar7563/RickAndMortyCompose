package com.example.rickandmortymvvm.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rickandmortymvvm.domain.model.Characters
import javax.inject.Inject

class CommonRepository @Inject constructor(){
    private var charactersList: MutableList<Characters> = mutableListOf()

    fun getCharacters(): MutableList<Characters> = charactersList
    fun setCharacters(characters: MutableList<Characters>?) {
        if (characters != null) {
            charactersList = characters
        }
    }
}