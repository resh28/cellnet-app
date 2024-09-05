package com.example.cellnet.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.cellnet.core.common.model.NavigationRoutes
import com.example.cellnet.feature.authentication.navigation.authScreen
import com.example.cellnet.feature.authentication.navigation.navigateToAuth
import com.example.cellnet.feature.dashboard.navigation.dashboardScreen
import com.example.cellnet.feature.home.navigation.homeScreen
import com.example.cellnet.feature.home.navigation.navigateToHome
import com.example.cellnet.feature.settings.navigation.settingsScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

val homeNavigationRoute = NavigationRoutes.HomeRoute.path
val authNavigationRoute = NavigationRoutes.AuthRoute.path

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CellnetNavHost(
    navController: NavHostController
) {
    var auth = Firebase.auth
//    auth.signOut()

    NavHost(
        navController = navController,
        startDestination =
            if(auth.currentUser!= null)
                homeNavigationRoute
            else authNavigationRoute
    ){
        homeScreen()
        dashboardScreen()
        settingsScreen(
            navigationToAuthScreen = {
                navController.popBackStack(navController.graph.findStartDestination().id, true)
                navController.navigateToAuth(it)
            }
        )
        authScreen(
            navigationToHomeScreen = {
                navController.popBackStack()
                navController.navigateToHome(it)
            }
        )
    }
}