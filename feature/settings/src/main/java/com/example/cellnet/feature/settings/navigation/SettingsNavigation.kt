package com.example.cellnet.feature.settings.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.cellnet.core.common.model.NavigationRoutes
import com.example.cellnet.feature.settings.SettingsRoute

val settingsNavigationRoute = NavigationRoutes.SettingsRoute.path

fun NavController.navigateToSettings(
    navOptions: NavOptions? = null,
) {
    this.navigate(settingsNavigationRoute, navOptions)
}

@RequiresApi(Build.VERSION_CODES.Q)
fun NavGraphBuilder.settingsScreen(
    navigationToAuthScreen: (NavOptions?) -> Unit
) {
    composable(
        route =  settingsNavigationRoute,
    ) {
        SettingsRoute(
            navigateToAuthScreen = navigationToAuthScreen
        )
    }
}