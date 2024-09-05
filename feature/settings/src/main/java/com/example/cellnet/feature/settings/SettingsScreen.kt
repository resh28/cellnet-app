package com.example.cellnet.feature.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.example.cellnet.core.common.Util
import com.example.cellnet.core.designsystem.appSnackbarHost.AppSnackBarHost

@Composable
internal fun SettingsRoute(
    modifier: Modifier = Modifier,
    navigateToAuthScreen: (NavOptions?) -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    SettingsScreen(modifier = modifier, settingsViewModel= settingsViewModel, navigateToAuthScreen = navigateToAuthScreen)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun SettingsScreen(
    modifier: Modifier,
    settingsViewModel: SettingsViewModel,
    navigateToAuthScreen: (NavOptions?) -> Unit,
) {
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarNotificationFlow by Util.getSnackbarFlow().collectAsStateWithLifecycle()

    LaunchedEffect(key1 = snackBarNotificationFlow) {
        if (snackBarNotificationFlow.second != "") {
            snackbarHostState.showSnackbar(
                message = snackBarNotificationFlow.second,
                actionLabel = "",
                duration = SnackbarDuration.Short,
            )
        }
    }

    Scaffold(
        snackbarHost = {
            AppSnackBarHost(
                snackbarHostState = snackbarHostState,
                modifier = modifier,
                componentHeight = remember { mutableStateOf(20.dp) },
                infoLevel = snackBarNotificationFlow.first
            )
        },
    )
    {
        Column(
            modifier = modifier
                .padding(20.dp),
        ) {
            ElevatedCard(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            ) {
                Column(
                    modifier = modifier
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "",
                            modifier = modifier
                                .size(80.dp)
                                .padding(end = 14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "${settingsUiState.userData.firstName} ${settingsUiState.userData.lastName}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = settingsUiState.userData.email,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }


            ElevatedCard(
                onClick = {
                    settingsViewModel.userSignOut()
                    navigateToAuthScreen(
                        navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            ) {
                Column(
                    modifier = modifier
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "",
                            modifier = modifier
//                                .size(80.dp)
                                .padding(end = 10.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "Log out",
                            )
                        }
                    }
                }
            }
        }
    }
}