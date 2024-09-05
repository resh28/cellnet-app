package com.example.cellnet.feature.home.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.cellnet.core.common.model.NavigationRoutes
import com.example.cellnet.feature.home.HomeRoute

val homeNavigationRoute = NavigationRoutes.HomeRoute.path

fun NavController.navigateToHome(
    navOptions: NavOptions? = null,
) {
    this.navigate(homeNavigationRoute, navOptions)
}

@RequiresApi(Build.VERSION_CODES.Q)
fun NavGraphBuilder.homeScreen(

) {
    composable(
        route =  homeNavigationRoute,
    ) {
        HomeRoute()
    }
}