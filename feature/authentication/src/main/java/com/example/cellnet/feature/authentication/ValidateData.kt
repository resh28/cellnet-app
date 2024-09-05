package com.example.cellnet.feature.authentication

import android.util.Patterns
import com.example.cellnet.core.common.model.ValidationResult
import javax.inject.Inject

class ValidateData @Inject constructor() {

    fun validateEmail(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter an e-mail address"
            )
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter an valid e-mail address"
            )
        }
        else {
            return ValidationResult(
                successful = true,
                errorMessage = ""
            )
        }
    }

    fun validatePassword(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter password"
            )
        } else if (password.length <= 6){
            return ValidationResult(
                successful = false,
                errorMessage = "Password should at least have 7 characters"
            )
        }
        else {
            return ValidationResult(
                successful = true,
                errorMessage = ""
            )
        }
    }

    fun validateFirstName(firstName: String): ValidationResult {
        if (firstName.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter first name"
            )
        } else {
            return ValidationResult(
                successful = true,
                errorMessage = ""
            )
        }
    }

    fun validateLastName(lastName: String): ValidationResult {
        if (lastName.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter last name"
            )
        } else {
            return ValidationResult(
                successful = true,
                errorMessage = ""
            )
        }
    }

    fun validateConfirmPassword(confirmPassword: String, password: String): ValidationResult {
        if (confirmPassword.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please confirm password"
            )
        } else if (password != confirmPassword){
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter the same password"
            )
        } else {
            return ValidationResult(
                successful = true,
                errorMessage = ""
            )
        }
    }

}