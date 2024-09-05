package com.example.cellnet.feature.authentication.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.cellnet.core.common.model.NavigationRoutes
import com.example.cellnet.feature.authentication.AuthRoute

val authNavigationRoute = NavigationRoutes.AuthRoute.path

fun NavController.navigateToAuth(
    navOptions: NavOptions? = null,
) {
    this.navigate(authNavigationRoute, navOptions)
}

fun NavGraphBuilder.authScreen(
    navigationToHomeScreen: (NavOptions?) -> Unit
) {
    composable(
        route =  authNavigationRoute,
    ) {
        AuthRoute(
            navigateToHomeScreen = navigationToHomeScreen
        )
    }
}