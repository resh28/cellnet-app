package com.example.cellnet.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cellnet.core.data.iRepository.FirebaseRepository
import com.example.cellnet.core.data.iRepository.LocalStorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val localStorageRepository: LocalStorageRepository,
    private val firebaseRepository: FirebaseRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            localStorageRepository.getUser().collect {user ->
                _uiState.update { currentState ->
                    currentState.copy(
                        userData = user
                    )
                }
            }
        }
    }

    fun userSignOut(){
        viewModelScope.launch {
            firebaseRepository.signOutUser()
        }
    }


}