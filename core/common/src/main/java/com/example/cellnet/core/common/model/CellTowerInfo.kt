package com.example.cellnet.core.common.model

import kotlinx.serialization.Serializable

@Serializable
data class CellTowerInfo(
    val uId: String = "",
    val cid: Int? = null,
    val lac: Int? = null,
    val mcc: String = "",
    val mnc: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
)

data class cellTowerData(
    val cid: Int? = null,
    val lac: Int? = null,
)
