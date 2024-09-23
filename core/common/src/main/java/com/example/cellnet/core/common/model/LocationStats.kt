package com.example.cellnet.core.common.model

data class LocationStats(
    val latitude: Double,
    val longitude: Double,
    val averageSignalStrength: Double,
    val averageDownloadSpeed: Int,
    val averageUploadSpeed: Int
)
