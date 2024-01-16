package com.example.rickandmortymvvm.di

import com.example.rickandmortymvvm.data.repositories.CommonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideCommonRepository(): CommonRepository {
        return CommonRepository()
    }
}