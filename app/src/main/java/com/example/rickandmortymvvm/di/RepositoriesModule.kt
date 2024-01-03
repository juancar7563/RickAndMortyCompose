package com.example.rickandmortymvvm.di

import com.example.rickandmortymvvm.data.repositories.CharacterRepositoryImpl
import com.example.rickandmortymvvm.domain.repositories.CharacterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoriesModule {

    @Binds
    abstract fun bindCharacterRepository(impl: CharacterRepositoryImpl): CharacterRepository
}