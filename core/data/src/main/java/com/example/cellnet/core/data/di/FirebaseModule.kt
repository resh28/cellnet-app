package com.example.cellnet.core.data.di

import com.example.cellnet.core.data.repository.DefaultFirebaseRepository
import com.example.cellnet.core.data.iRepository.FirebaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseRepository(): FirebaseRepository {
        return DefaultFirebaseRepository()
    }
}