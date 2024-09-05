package com.example.cellnet.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.cellnet.core.data.iRepository.LocalStorageRepository
import com.example.cellnet.core.data.repository.DefaultLocalStorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalStorageModule {

    @Provides
    @Singleton
    fun provideLocalStorageRepository(
        dataStore: DataStore<Preferences>
    ): LocalStorageRepository {
        return DefaultLocalStorageRepository(dataStore)
    }
}