package com.example.cellnet.feature.authentication

data class AuthUiState(
    val isSignIn: Boolean = true,
    val email: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val confirmPassword: String = "",

    val emailError: String = "",
    val passwordError: String = "",
    val firstNameError: String = "",
    val lastNameError: String = "",
    val confirmPasswordError: String = "",

    val isLoading: Boolean = false,
)
