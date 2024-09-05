package com.example.cellnet.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.cellnet.core.common.model.User
import com.example.cellnet.core.data.iRepository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultLocalStorageRepository @Inject constructor (
    private val dataStore: DataStore<Preferences>
) : LocalStorageRepository {
    private val firstName = stringPreferencesKey("FIRST_NAME")
    private val lastName = stringPreferencesKey("LAST_NAME")
    private val email = stringPreferencesKey("EMAIL")
    private val userId = stringPreferencesKey("USER_UID")

    override suspend fun saveUser(
        user: User
    ) {
        dataStore.edit { preferences ->
            preferences[firstName] = user.firstName
            preferences[lastName] = user.lastName
            preferences[email] = user.email
            preferences[userId] = user.userId
        }
    }

    override fun getUser(): Flow<User> {
        return dataStore.data.map { prefs ->
            val defUser = User()
            User(
                prefs[userId] ?: defUser.userId,
                prefs[firstName] ?: defUser.firstName,
                prefs[lastName] ?: defUser.lastName,
                prefs[email] ?: defUser.email,
            )
        }
    }
}