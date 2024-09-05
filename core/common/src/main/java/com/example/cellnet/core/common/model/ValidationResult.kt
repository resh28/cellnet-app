package com.example.cellnet.core.common.model

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String = "",
)
