package com.example.cellnet.feature.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cellnet.core.common.LocationUtil
import com.example.cellnet.core.common.Util
import com.example.cellnet.core.designsystem.appSnackbarHost.AppSnackBarHost
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
internal fun HomeRoute(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    HomeScreen(modifier = modifier, homeViewModel = homeViewModel)
}

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "DefaultLocale")
@Composable
internal fun HomeScreen(
    modifier: Modifier,
    homeViewModel: HomeViewModel,
) {
    val homeUiState by homeViewModel.uiState.collectAsState()

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarNotificationFlow by Util.getSnackbarFlow().collectAsStateWithLifecycle()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasReadPhoneStatePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_PHONE_STATE
    )

    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        permissionsMap.forEach { (permission, granted) ->
            if (granted) {
                if (permission == "android.permission.ACCESS_FINE_LOCATION")
                    hasLocationPermission = true
                if (permission == "android.permission.READ_PHONE_STATE")
                    hasReadPhoneStatePermission = true
                Log.d("Permission Granted", "$permission is granted")
            } else {
                Log.d("Permission Denied", "$permission is denied")
            }
        }
    }

    LaunchedEffect(Unit) {
        multiplePermissionsLauncher.launch(permissions.toTypedArray())
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasLocationPermission = granted
            if (!granted) {
                Log.d("Location Permission", "Location Permission not granted")
            }
        }
    )

    val readPhoneStatePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (!granted) {
                Log.d("ReadPhoneState Permission", "ReadPhoneState Permission not granted")
            }
        }
    )

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
//        topBar = {
//            TopAppBar(
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.primary,
//                ),
//                title = {
//                    Text("Cellnet Dashboard")
//                }
//            )
//        },
        snackbarHost = {
            AppSnackBarHost(
                snackbarHostState = snackbarHostState,
                modifier = modifier,
                componentHeight = remember { mutableStateOf(20.dp) },
                infoLevel = snackBarNotificationFlow.first
            )
        },
    ) {innerPadding ->
        Column(
            modifier = modifier
                .padding(20.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Cellnet",
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
//                if (homeUiState.isScanData)
//                    Button(
//                        onClick = {
//                            homeViewModel.updateIsDataUploading(true)
//                            homeViewModel.uploadData()
//                        },
//                        enabled = !homeUiState.isDataUploaded && !homeUiState.isDataUploading
//                    ) {
//                        Text(text = "Upload Data")
//                        if (homeUiState.isDataUploading)
//                            CircularProgressIndicator(
//                                modifier = modifier
//                                    .size(24.dp)
//                                    .padding(start = 5.dp),
//                                strokeWidth = 2.dp,
//                                color = Color.Gray
//                            )
//                    }
            }
            Text(
                text = "Hello, welcome ${homeUiState.userData.firstName}. Let's explore about your network information.",
                color = Color.Gray
            )

            if (!hasLocationPermission && !hasReadPhoneStatePermission) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = modifier
                            .fillMaxWidth(0.75f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "",
                            modifier = modifier.size(200.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = "To use the application you need to give couple of permissions first. Please click below button and follow steps to grant required permissions",
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { multiplePermissionsLauncher.launch(permissions.toTypedArray()) },
                            modifier = modifier
                                .padding(top = 10.dp)
                        ) {
                            Text(text = "Grant Permissions")
                        }
                    }
                }
            } else {
                if (!homeUiState.isScanData){
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(bottom = 100.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(224.dp)
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (homeUiState.isScanning)
                                CircularProgressIndicator(
                                    modifier = Modifier.size(220.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 10.dp,
                                )
                            ElevatedCard(
                                onClick = {
                                    homeViewModel.updateIsScanning(true)
                                    homeViewModel.updateIsDataUploaded(false)
                                    homeViewModel.onScan(context)
                                },
                                enabled = !homeUiState.isScanning,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 10.dp
                                ),
                                modifier = Modifier
                                    .size(200.dp),
                                shape = CircleShape
                            ) {
                                Column(
                                    modifier = modifier
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    ElevatedCard(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 100.dp
                                        ),
                                        modifier = Modifier
                                            .size(170.dp),
                                        shape = CircleShape
                                    ) {
                                        Column(
                                            modifier = modifier
                                                .fillMaxSize(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "SCAN",
                                                modifier = Modifier,
                                                textAlign = TextAlign.Center,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "NETWORK",
                                                modifier = Modifier,
                                                textAlign = TextAlign.Center,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "INFO",
                                                modifier = Modifier,
                                                textAlign = TextAlign.Center,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        ElevatedCard(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            Column(
                                modifier = modifier
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = "Device Information",
                                    fontWeight = FontWeight.Bold,
                                    modifier = modifier
                                        .padding(bottom = 5.dp)
                                )
                                Row {
                                    Text(text = "Name: ")
                                    Text(text = homeUiState.deviceInfo.productName, fontWeight = FontWeight.Light)
                                }
                                Row {
                                    Text(text = "Manufacturer: ")
                                    Text(text = homeUiState.deviceInfo.manufacturer, fontWeight = FontWeight.Light)
                                }
                                Row {
                                    Text(text = "Phone Type: ")
                                    Text(text = homeUiState.deviceInfo.phoneType, fontWeight = FontWeight.Light)
                                }
                                Row {
                                    Text(text = "Operating System: ")
                                    Text(text = homeUiState.deviceInfo.osVersion, fontWeight = FontWeight.Light)
                                }
                                Row {
                                    Text(text = "App Version: ")
                                    Text(text = homeUiState.deviceInfo.appVersion, fontWeight = FontWeight.Light)
                                }
                            }
                        }

                        Row(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ElevatedCard(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onTertiary
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(0.47f)
                                    .padding(end = 20.dp),
                            ) {
                                Column(
                                    modifier = modifier
                                        .padding(20.dp)
                                ) {
                                    Text(
                                        text = "Timestamp",
                                        fontWeight = FontWeight.Bold,
                                        modifier = modifier
                                            .padding(bottom = 5.dp)
                                    )
                                    Text(text = LocalDateTime.ofInstant(homeUiState.dateTime?.toInstant() , ZoneId.systemDefault()).format(Util.dateFormatter), fontWeight = FontWeight.Light)
                                    Text(text = LocalDateTime.ofInstant(homeUiState.dateTime?.toInstant() , ZoneId.systemDefault()).format(Util.timeFormatter), fontWeight = FontWeight.Light)
                                }
                            }
                            ElevatedCard(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onTertiary
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = modifier
                                        .padding(20.dp)
                                ) {
                                    Text(
                                        text = "Device Location",
                                        fontWeight = FontWeight.Bold,
                                        modifier = modifier
                                            .padding(bottom = 5.dp)
                                    )
                                    Text(text = LocationUtil.getLocationName(context, homeUiState.currentLocation.latitude, homeUiState.currentLocation.longitude), fontWeight = FontWeight.Light)
                                    Text(
                                        text = "lat ${String.format("%.4f", homeUiState.currentLocation.latitude)}, lng ${String.format("%.4f", homeUiState.currentLocation.longitude)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Light
                                    )
                                }
                            }
                        }

                        ElevatedCard(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                        ) {
                            Column(
                                modifier = modifier
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = "Network Information",
                                    fontWeight = FontWeight.Bold,
                                    modifier = modifier
                                        .padding(bottom = 5.dp)
                                )
                                Row {
                                    Text(text = "Network Operator: ")
                                    Text(text = homeUiState.networkOperator, fontWeight = FontWeight.Light)
                                }
                                Row {
                                    Text(text = "Network Class: ")
                                    Text(text = homeUiState.networkClass, fontWeight = FontWeight.Light)
                                }
                                Row {
                                    Text(text = "Downloading Speed: ")
                                    Text(text = "${homeUiState.networkDownSpeed} Mbps", fontWeight = FontWeight.Light)
                                }
                                Row {
                                    Text(text = "Uploading Speed: ")
                                    Text(text = "${homeUiState.networkUpSpeed} Mbps", fontWeight = FontWeight.Light)
                                }
                                Row {
                                    Text(text = "Signal Strength: ")
                                    Text(text = "${homeUiState.signalStrength} dBm", fontWeight = FontWeight.Light)
                                }

                            }
                        }

                        ElevatedCard(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                        ) {
                            Column(
                                modifier = modifier
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = "Cell Tower Information",
                                    fontWeight = FontWeight.Bold,
                                    modifier = modifier
                                        .padding(bottom = 5.dp)
                                )
                                Row {
                                    Text(text = "Mobile Country Code: ")
                                    Text(text = homeUiState.cellTowerInfo.mcc, fontWeight = FontWeight.Light)
                                }
                                Row {
                                    Text(text = "Mobile Network Code: ")
                                    Text(text = homeUiState.cellTowerInfo.mnc, fontWeight = FontWeight.Light)
                                }
                                Row {
                                    Text(text = "Cell Id: ")
                                    Text(text = "${homeUiState.cellTowerInfo.cid}", fontWeight = FontWeight.Light)
                                }
                                Row {
                                    Text(text = "LAC/TAC: ")
                                    Text(text = "${homeUiState.cellTowerInfo.lac}", fontWeight = FontWeight.Light)
                                }
                                Row {
                                    Text(text = "Latitude: ")
                                    Text(text = "${homeUiState.cellTowerInfo.lat}", fontWeight = FontWeight.Light)
                                }
                                Row {
                                    Text(text = "Longitude: ")
                                    Text(text = "${homeUiState.cellTowerInfo.lng}", fontWeight = FontWeight.Light)
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                homeViewModel.updateIsScanning(true)
                                homeViewModel.onScan(context)
                            },
                            modifier = modifier
                                .padding(top = 20.dp, bottom = 80.dp)
                                .fillMaxWidth(),
                            enabled = !homeUiState.isScanning && !homeUiState.isDataUploading
                        ) {
                            Text(text = "Scan Again")
                            if (homeUiState.isScanning)
                                CircularProgressIndicator(
                                    modifier = modifier
                                        .size(24.dp)
                                        .padding(start = 5.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.Gray
                                )
                        }
                    }
                }
            }
        }
    }
}