package com.example.cellnet.feature.home

import android.location.Location
import com.example.cellnet.core.common.model.CellTowerInfo
import com.example.cellnet.core.common.model.DeviceInfo
import com.example.cellnet.core.common.model.User
import java.util.Date

data class HomeUiState(
    val userData: User = User(),
    val deviceInfo: DeviceInfo = DeviceInfo(),
    val cellTowerInfo: CellTowerInfo = CellTowerInfo(),


    val networkDownSpeed: Int = 0,
    val networkUpSpeed: Int = 0,
    val networkClass: String = "",
    val signalStrength: Int? = null,
    val currentLocation: Location = Location(""),
    val dateTime: Date? = null,
    val networkOperator: String = "",
    val phoneType: String = "",
    val wifiSSID: String = "",

    val isScanData: Boolean = false,
    val isScanning: Boolean = false,
    val isDataUploaded: Boolean = false,
    val isDataUploading: Boolean = false
)
