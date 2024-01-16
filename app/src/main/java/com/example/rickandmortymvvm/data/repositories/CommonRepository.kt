package com.example.rickandmortymvvm.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rickandmortymvvm.domain.model.Characters
import javax.inject.Inject

class CommonRepository @Inject constructor(){
    private val charactersList = MutableLiveData<List<Characters>>()
    fun getCharacters(): LiveData<List<Characters>> = charactersList
    fun setCharacters(characters: MutableList<Characters>?) {
        charactersList.value = characters
    }
}