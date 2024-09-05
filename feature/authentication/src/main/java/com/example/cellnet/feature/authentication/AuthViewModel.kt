package com.example.cellnet.feature.authentication

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.example.cellnet.core.common.Util
import com.example.cellnet.core.common.model.SnackbarInfoLevel
import com.example.cellnet.core.common.model.User
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
class AuthViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val localStorageRepository: LocalStorageRepository,
    private val validateData: ValidateData
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var signInHasError: Boolean = false
    private var signUpHasError: Boolean = false

    fun toggleAuthScreen() {
        _uiState.update { currentState ->
            currentState.copy(
                isSignIn = !_uiState.value.isSignIn,
                email = "",
                password = "",
                firstName = "",
                lastName = "",
                confirmPassword = "",
                emailError = "",
                passwordError = "",
                firstNameError = "",
                lastNameError = "",
                confirmPasswordError = "",
            )
        }
        signInHasError = false
        signUpHasError = false
    }

    fun updateEmail(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                email = value
            )
        }
    }

    fun updatePassword(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = value
            )
        }
    }

    fun updateFirstName(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                firstName = value
            )
        }
    }

    fun updateLastName(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                lastName = value
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

    fun updateIsLoading(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = value
            )
        }
    }

    fun userSignUp(navigateToHomeScreen: (NavOptions?) -> Unit, context: Context) {
        viewModelScope.launch {
            validateSignUp()
            if (!signUpHasError){
                val user = User(
                    "",
                    _uiState.value.firstName,
                    _uiState.value.lastName,
                    _uiState.value.email
                )
                val result = firebaseRepository.signUpUser(_uiState.value.email, _uiState.value.password, user)
                result.onSuccess {
                    user.userId = it
                    Log.d("user", user.toString())
                    saveUserOnDataStore(user)
                    saveDeviceInfo(it, context)
                    navigateToHomeScreen(navOptions {
                        launchSingleTop = true
                    })
                }.onFailure { exception ->
                    // Sign-up failed, handle the error
                    Log.w("Auth", "createUserWithEmail:failure", exception)
                    Util.showSnackbar(SnackbarInfoLevel.ERROR, "Sign up failure: ${exception.message}")
                }
            }
            updateIsLoading(false)
        }
    }

    fun userSignIn(navigateToHomeScreen: (NavOptions?) -> Unit, context: Context) {
        viewModelScope.launch {
            validateSignIn()
            if (!signInHasError) {
                val result = firebaseRepository.signInUser(_uiState.value.email, _uiState.value.password)
                result.onSuccess {
                    getUser(it)
                    saveDeviceInfo(it, context)
                    navigateToHomeScreen(navOptions {
                        launchSingleTop = true
                    })
                }.onFailure { exception ->
                    Log.w("Auth", "SignInUserWithEmail:failure", exception)
                    Util.showSnackbar(SnackbarInfoLevel.ERROR, "Sign in failure: ${exception.message}")
                }
            }
            updateIsLoading(false)
        }
    }

    private suspend fun getUser(uid: String) {
        val result = firebaseRepository.getUser(uid)
        result.onSuccess {
            val user = it.toObject(User::class.java)
            Log.d("user:getUser", user.toString())
            user?.let {
                saveUserOnDataStore(user)
            }
        }.onFailure { exception ->
            Log.w("GetUser", "getUser:failure", exception)
        }
    }

    private suspend fun saveDeviceInfo(userId: String, context: Context) {
        try {
            val deviceInfo = Util.getDeviceInfo(context)
            deviceInfo.userId = userId
            firebaseRepository.saveDeviceInfo(deviceInfo)
        } catch (e: Exception) {
            Log.e("saveDeviceInfo", e.toString())
        }

    }

    private suspend fun saveUserOnDataStore(user: User){
        localStorageRepository.saveUser(user)
    }

    private fun validateSignIn() {
        val emailResult = validateData.validateEmail(_uiState.value.email)
        val passwordResult = validateData.validatePassword(_uiState.value.password)

        signInHasError = listOf(
            emailResult,
            passwordResult,
        ).any {
            !it.successful
        }

        _uiState.update { currentState ->
            currentState.copy(
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage
            )
        }
    }

    private fun validateSignUp() {
        val firstNameResult = validateData.validateFirstName(_uiState.value.firstName)
        val lastNameResult = validateData.validateLastName(_uiState.value.lastName)
        val emailResult = validateData.validateEmail(_uiState.value.email)
        val passwordResult = validateData.validatePassword(_uiState.value.password)
        val confirmPasswordResult = validateData.validateConfirmPassword(_uiState.value.confirmPassword, _uiState.value.password)

        signUpHasError = listOf(
            firstNameResult,
            lastNameResult,
            emailResult,
            passwordResult,
            confirmPasswordResult,
        ).any {
            !it.successful
        }

        _uiState.update { currentState ->
            currentState.copy(
                firstNameError = firstNameResult.errorMessage,
                lastNameError = lastNameResult.errorMessage,
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage,
                confirmPasswordError = confirmPasswordResult.errorMessage
            )
        }
    }

    fun validateOnFocusChange(){
        if(signInHasError)
            validateSignIn()
        if(signUpHasError)
            validateSignUp()
    }

}