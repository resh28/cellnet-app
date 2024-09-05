package com.example.cellnet.feature.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cellnet.core.common.Util
import com.example.cellnet.core.designsystem.appSnackbarHost.AppSnackBarHost

@Composable
internal fun DashboardRoute(
    modifier: Modifier = Modifier
){
    DashboardScreen(modifier = modifier)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun DashboardScreen(
    modifier: Modifier
){
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
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                        }
                    )
                }
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 40.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Coming Soon")
        }
    }
}