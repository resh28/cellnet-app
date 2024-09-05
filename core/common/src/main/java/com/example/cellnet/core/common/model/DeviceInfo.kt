package com.example.cellnet.core.common.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfo(
    val androidId: String = "",
    val productName: String = "",
    val model: String = "",
    val manufacturer: String = "",
    val osVersion: String = "",
    val appVersion: String = "",
    var userId: String = "",
    val phoneType: String = "",
)

