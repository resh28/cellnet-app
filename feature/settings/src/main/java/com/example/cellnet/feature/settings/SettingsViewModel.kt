package com.example.cellnet.feature.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cellnet.core.common.DataValidationUtil
import com.example.cellnet.core.common.Util
import com.example.cellnet.core.common.model.AppTheme
import com.example.cellnet.core.common.model.SnackbarInfoLevel
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

    private var hasError: Boolean = false

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

    init {
        viewModelScope.launch {
            localStorageRepository.getAppTheme().collect {appTheme ->
                _uiState.update { currentState ->
                    currentState.copy(
                        appTheme = appTheme
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

    fun updateApplicationTheme(appTheme: AppTheme){
        viewModelScope.launch {
            localStorageRepository.saveAppTheme(appTheme)
        }
    }

    fun updateIsLoading(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = value
            )
        }
    }

    fun updateShowPasswordChangeBottomSheet(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                showPasswordChangeBottomSheet = value
            )
        }
    }

    fun updateShowThemeChangeBottomSheet(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                showThemeChangeBottomSheet = value
            )
        }
    }

    fun updateCurrentPassword(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                currentPassword = value
            )
        }
    }

    fun updateNewPassword(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                newPassword = value
            )
        }
    }

    fun updateConfirmPassword(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                confirmPassword = value
            )
        }
    }

    private fun validatePasswords() {
        val currentPasswordResult = DataValidationUtil.validatePassword(_uiState.value.currentPassword)
        val newPasswordResult = DataValidationUtil.validatePassword(_uiState.value.newPassword)
        val confirmPasswordResult = DataValidationUtil.validateConfirmPassword(_uiState.value.confirmPassword, _uiState.value.newPassword)

        hasError = listOf(
            currentPasswordResult,
            newPasswordResult,
            confirmPasswordResult,
        ).any {
            !it.successful
        }

        _uiState.update { currentState ->
            currentState.copy(
                currentPasswordError = currentPasswordResult.errorMessage,
                newPasswordError = newPasswordResult.errorMessage,
                confirmPasswordError = confirmPasswordResult.errorMessage
            )
        }
    }

    fun validateOnFocusChange(){
        if(hasError)
            validatePasswords()
    }

    fun updatePassword() {
        viewModelScope.launch {
            validatePasswords()
            if (!hasError) {
                val result = firebaseRepository.changePassword(_uiState.value.currentPassword, _uiState.value.newPassword)
                result.onSuccess {
                    updateShowPasswordChangeBottomSheet(false)
                    Util.showSnackbar(SnackbarInfoLevel.SUCCESS, "Password changed successfully.")
                }.onFailure { exception ->
                    updateShowPasswordChangeBottomSheet(false)
                    Log.w("Auth", "SignInUserWithEmail:failure", exception)
                    Util.showSnackbar(SnackbarInfoLevel.ERROR, "Password change failure: ${exception.message}")
                }
            }
            updateCurrentPassword("")
            updateNewPassword("")
            updateConfirmPassword("")
            updateIsLoading(false)
        }
    }
}