package com.example.cellnet.core.common.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
)