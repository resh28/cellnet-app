package com.example.cellnet.feature.dashboard

import com.example.cellnet.core.common.model.CellTowerInfo
import com.example.cellnet.core.common.model.LocationStats
import com.example.cellnet.core.common.model.NetworkInfo
import com.google.android.gms.maps.model.LatLng

data class DashboardUiState(
    val currentPage: DashboardTabItem = DashboardTabItem.Stats,
    val durationOfData: String = "10",
    val durationOfDataTextFiledValue: String = durationOfData,
    val networkInfos: List<NetworkInfo> = emptyList<NetworkInfo>(),
    val cellTowerInfos: List<CellTowerInfo> = emptyList<CellTowerInfo>(),
    val isLoadingNetworkInfos: Boolean = true,
    val isLoadingCellTowerInfos: Boolean = true,
    val isLoadingMap: Boolean = true,
    val isNetworkInfoFetchError: Boolean = false,
    val isCellTowerInfoFetchError: Boolean = false,
    val showFilterBottomSheet: Boolean = false,

    val avgDownloadSpeed: Double = 0.0,
    val avgUploadSpeed: Double = 0.0,
    val avgSignalStrength: Int = 0,

    val frequentlyConnectedTowerInfo: CellTowerInfo? = null,

    val locationScores:  List<Pair<LocationStats, Double>> = emptyList(),
    val currentLocation: LatLng = LatLng(0.0, 0.0)
)
