package com.example.cellnet.core.data.iRepository

import com.example.cellnet.core.common.model.AppTheme
import com.example.cellnet.core.common.model.User
import kotlinx.coroutines.flow.Flow

interface LocalStorageRepository {

    suspend fun saveUser(
        user: User
    )

    fun getUser(): Flow<User>

    suspend fun saveAppTheme(
        applicationTheme: AppTheme
    )

    fun getAppTheme(): Flow<AppTheme>
}