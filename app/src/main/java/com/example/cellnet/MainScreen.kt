package com.example.cellnet

//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cellnet.core.common.model.NavigationRoutes
import com.example.cellnet.navigation.CellnetNavHost

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != NavigationRoutes.AuthRoute.path) {
                BottomNavigation(
                    backgroundColor = MaterialTheme.colorScheme.background,
//                    contentColor = MaterialTheme.colorScheme.primary,
                    elevation = 16.dp,

                ) {
                    val items = listOf(
                        NavigationRoutes.HomeRoute,
                        NavigationRoutes.DashboardRoute,
                        NavigationRoutes.SettingsRoute
                    )

                    items.forEach { item ->
                        BottomNavigationItem(
                            icon = { Icon(item.icon!!, contentDescription = item.title) },
                            label = { Text(
                                text = item.title!!,
                                color = if (currentRoute == item.path) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                modifier = modifier
                                    .padding(top = 4.dp),
                                fontSize = 14.sp
                            ) },
                            selected = currentRoute == item.path,
                            onClick = {
                                navController.navigate(item.path) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                    launchSingleTop = true
                                }
                            },
                            modifier = modifier
                                .padding(bottom = 15.dp),
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    ) {
        CellnetNavHost(navController)
    }
}