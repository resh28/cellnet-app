package com.example.cellnet.feature.dashboard

import com.example.cellnet.core.common.model.CellTowerInfo
import com.example.cellnet.core.common.model.NetworkInfo

data class DashboardUiState(
    val currentPage: DashboardTabItem = DashboardTabItem.Stats,
    val durationOfData: String = "5",
    val durationOfDataTextFiledValue: String = durationOfData,
    val networkInfos: List<NetworkInfo> = emptyList<NetworkInfo>(),
    val cellTowerInfos: List<CellTowerInfo> = emptyList<CellTowerInfo>(),
    val isLoadingNetworkInfos: Boolean = true,
    val isLoadingCellTowerInfos: Boolean = true,
    val isNetworkInfoFetchError: Boolean = false,
    val isCellTowerInfoFetchError: Boolean = false,
    val showFilterBottomSheet: Boolean = false,

    val avgDownloadSpeed: Double = 0.0,
    val avgUploadSpeed: Double = 0.0,
    val avgSignalStrength: Int = 0,
)
