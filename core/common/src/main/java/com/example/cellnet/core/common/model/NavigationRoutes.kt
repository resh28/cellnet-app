package com.example.cellnet.core.common.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationRoutes(val path: String, val icon: ImageVector? = null, val title: String? = null) {
    AuthRoute("auth_route"),
    HomeRoute("home_route", Icons.Default.Home, "Home"),
    DashboardRoute("dashboard_route", Icons.Default.Dashboard, "Dashboard"),
    SettingsRoute("settings_route", Icons.Default.Settings, "Settings")
}