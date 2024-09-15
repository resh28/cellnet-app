package com.example.cellnet.feature.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.ui.graphics.vector.ImageVector

sealed class DashboardTabItem(var icon: ImageVector, var title: String) {
    data object Stats: DashboardTabItem(Icons.Default.QueryStats, "Stats")
    data object NetworkExperience: DashboardTabItem(Icons.Default.SignalCellularAlt, "Network Info")
}