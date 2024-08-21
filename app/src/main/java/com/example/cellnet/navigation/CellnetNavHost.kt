package com.example.cellnet.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.cellnet.core.common.model.NavigationRoutes
import com.example.cellnet.feature.home.navigation.homeScreen

val homeNavigationRoute = NavigationRoutes.HomeRoute.path

@Composable
fun CellnetNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = homeNavigationRoute
    ){
        homeScreen()
    }
}