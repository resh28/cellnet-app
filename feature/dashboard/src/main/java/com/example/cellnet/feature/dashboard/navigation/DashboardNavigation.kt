package com.example.cellnet.feature.dashboard.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.cellnet.core.common.model.NavigationRoutes
import com.example.cellnet.feature.dashboard.DashboardRoute

val dashboardNavigationRoute = NavigationRoutes.DashboardRoute.path

fun NavController.navigateToDashboard(
    navOptions: NavOptions? = null,
) {
    this.navigate(dashboardNavigationRoute, navOptions)
}

@RequiresApi(Build.VERSION_CODES.Q)
fun NavGraphBuilder.dashboardScreen(

) {
    composable(
        route =  dashboardNavigationRoute,
    ) {
        DashboardRoute()
    }
}