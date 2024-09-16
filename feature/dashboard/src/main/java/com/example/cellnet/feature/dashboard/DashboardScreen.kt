package com.example.cellnet.feature.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cellnet.core.common.LocationUtil
import com.example.cellnet.core.common.Util
import com.example.cellnet.core.designsystem.appSnackbarHost.AppSnackBarHost
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun DashboardRoute(
    modifier: Modifier = Modifier,
    dashboardViewModel: DashboardViewModel = hiltViewModel()
){
    DashboardScreen(
        modifier = modifier,
        dashboardViewModel = dashboardViewModel,
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun DashboardScreen(
    modifier: Modifier,
    dashboardViewModel: DashboardViewModel,
){
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarNotificationFlow by Util.getSnackbarFlow().collectAsStateWithLifecycle()

    val tabs = listOf(DashboardTabItem.Stats, DashboardTabItem.NetworkExperience)

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
                .padding(20.dp)
                .padding(top = 24.dp),
        ) {
            Text(
                text = "Cellnet Dashboard",
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Let's explore statistics of your network connections.",
                color = Color.Gray
            )

            Row(
                modifier = modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "* Data based on last ${dashboardUiState.durationOfData} days.",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Light
                )
                Row {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "refresh",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = modifier
                            .padding(end = 10.dp)
                            .clickable {
                                dashboardViewModel.fetchData()
                            },
                    )
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "filter",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            DashboardTabRow(
                tabs = tabs,
                currentPage = dashboardUiState.currentPage,
                onClickTab = {index: Int -> dashboardViewModel.updateCurrentPage(tabs[index])},
                modifier = modifier
            )

            if (dashboardUiState.isLoadingNetworkInfos && dashboardUiState.isLoadingCellTowerInfos) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Text(
                        text = "Loading data...",
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = modifier.padding(top = 10.dp)
                    )
                }
            } else if (dashboardUiState.isNetworkInfoFetchError && dashboardUiState.isCellTowerInfoFetchError){
                EmptyDataView(
                    modifier = modifier,
                    icon = Icons.Default.Error,
                    content = "Something went wrong while fetching the information. Please check your connection and try again later."
                )
            } else if (dashboardUiState.networkInfos.isEmpty() && dashboardUiState.cellTowerInfos.isEmpty()) {
                    EmptyDataView(
                        modifier = modifier,
                        icon = Icons.Default.Search,
                        content = "No data found for the last ${dashboardUiState.durationOfData} days"
                    )
            } else {
                when (dashboardUiState.currentPage) {
                    is DashboardTabItem.Stats -> {
                        StatsView(
                            modifier = modifier,
                            dashboardUiState = dashboardUiState,
                            dashboardViewModel = dashboardViewModel,
                        )
                    }
                    is DashboardTabItem.NetworkExperience -> {
                        NetworkInfoView(
                            modifier = modifier,
                            dashboardUiState = dashboardUiState,
                            dashboardViewModel = dashboardViewModel,
                            context = context
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun StatsView(
    modifier: Modifier,
    dashboardUiState: DashboardUiState,
    dashboardViewModel: DashboardViewModel
) {
    Column(
        modifier = modifier
            .padding(top = 20.dp)
    ) {
        Text(
            text = "Summary",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
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
                .padding(top = 10.dp, bottom = 10.dp),
        ) {
            Column(
                modifier = modifier
                    .padding(20.dp)
            ) {
                Row(
                    modifier = modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(0.85f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Download\nSpeed", fontWeight = FontWeight.Light, fontSize = 12.sp, style = TextStyle(lineHeight = 12.sp))
                        Text(text = "${dashboardUiState.avgDownloadSpeed}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(text = "Mbps", fontWeight = FontWeight.Light, fontSize = 12.sp, fontStyle = FontStyle.Italic)
                    }
                    Column {
                        Text(text = "Upload\nSpeed", fontWeight = FontWeight.Light, fontSize = 12.sp, style = TextStyle(lineHeight = 12.sp))
                        Text(text = "${dashboardUiState.avgUploadSpeed}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(text = "Mbps", fontWeight = FontWeight.Light, fontSize = 12.sp, fontStyle = FontStyle.Italic)
                    }
                    Column {
                        Text(text = "Signal\nStrength", fontWeight = FontWeight.Light, fontSize = 12.sp, style = TextStyle(lineHeight = 12.sp))
                        Text(text = "${dashboardUiState.avgSignalStrength}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(text = "dBm", fontWeight = FontWeight.Light, fontSize = 12.sp, fontStyle = FontStyle.Italic)
                    }
                }
            }
        }

        Text(
            text = "Frequently connected data",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier
                .padding(top = 20.dp)
        )
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
                .padding(top = 10.dp, bottom = 10.dp),
        ) {
            Column(
                modifier = modifier
                    .padding(20.dp)
            ) {
                Row(
                    modifier = modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(0.85f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Network\nOperator", fontWeight = FontWeight.Light, fontSize = 12.sp, style = TextStyle(lineHeight = 12.sp))
                        Text(text = dashboardViewModel.getFrequentNetworkOperator(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Column {
                        Text(text = "Network\nClass", fontWeight = FontWeight.Light, fontSize = 12.sp, style = TextStyle(lineHeight = 12.sp))
                        Text(text = dashboardViewModel.getFrequentNetworkClass(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun NetworkInfoView(
    modifier: Modifier,
    dashboardUiState: DashboardUiState,
    dashboardViewModel: DashboardViewModel,
    context: Context
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 50.dp)
    ) {
        items(dashboardUiState.networkInfos) { data ->
            val cellTowerData = dashboardViewModel.getCellTowerData(data.cellTowerId)
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
                    .padding(top = 10.dp, bottom = 10.dp),
            ) {
                Column(
                    modifier = modifier
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(
                            modifier = modifier
                                .fillMaxWidth(0.65f),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            Text(
                                text = "${data.networkOperator} | ${data.networkClass}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                style = TextStyle(lineHeight = 14.sp)
                            )
                            Text(
                                text = LocationUtil.getLocationName(context, data.latitude, data.longitude),
                                fontSize = 15.sp,
                                style = TextStyle(lineHeight = 14.sp)
                            )
                            Text(
                                text = LocalDateTime.ofInstant(data.timeStamp?.toInstant() , ZoneId.systemDefault()).format(Util.dateTimeFormatter),
                                fontWeight = FontWeight.Light,
                                fontSize = 12.sp
                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(imageVector = Icons.Default.CellTower, contentDescription = "CellTower")
                                Column {
                                    Text(text = "${cellTowerData?.cid}", modifier = modifier.padding(start = 8.dp), fontSize = 15.sp, style = TextStyle(lineHeight = 14.sp))
                                    Text(text = "lat: ${String.format("%.4f", cellTowerData?.lat)}", modifier = modifier.padding(start = 8.dp), fontWeight = FontWeight.Light, fontSize = 12.sp, style = TextStyle(lineHeight = 10.sp))
                                    Text(text = "lng: ${String.format("%.4f", cellTowerData?.lng)}", modifier = modifier.padding(start = 8.dp), fontWeight = FontWeight.Light, fontSize = 12.sp, style = TextStyle(lineHeight = 11.sp))
                                }
                            }
                        }
                    }
                    Row(
                        modifier = modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth(0.85f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Download\nSpeed", fontWeight = FontWeight.Light, fontSize = 12.sp, style = TextStyle(lineHeight = 12.sp))
                            Text(text = "${data.downloadSpeed}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = "Mbps", fontWeight = FontWeight.Light, fontSize = 12.sp, fontStyle = FontStyle.Italic)
                        }
                        Column {
                            Text(text = "Upload\nSpeed", fontWeight = FontWeight.Light, fontSize = 12.sp, style = TextStyle(lineHeight = 12.sp))
                            Text(text = "${data.uploadSpeed}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = "Mbps", fontWeight = FontWeight.Light, fontSize = 12.sp, fontStyle = FontStyle.Italic)
                        }
                        Column {
                            Text(text = "Signal\nStrength", fontWeight = FontWeight.Light, fontSize = 12.sp, style = TextStyle(lineHeight = 12.sp))
                            Text(text = "${data.signalStrength}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = "dBm", fontWeight = FontWeight.Light, fontSize = 12.sp, fontStyle = FontStyle.Italic)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun DashboardTabRow(
    tabs: List<DashboardTabItem>,
    currentPage : DashboardTabItem,
    onClickTab : (Int) -> Unit,
    modifier: Modifier
){
    TabRow(
        selectedTabIndex = tabs.indexOf(currentPage),
        containerColor = Color.Transparent,
        modifier = modifier.padding(top = 12.dp, bottom = 10.dp)
    ) {
        tabs.forEachIndexed { index, tab ->
            LeadingIconTab(
                selected = tabs.indexOf(currentPage) == index,
                onClick = { onClickTab(index) },
                text = { Text(text = tab.title, fontSize = 16.sp, ) },
                icon = { Icon(imageVector = tab.icon, contentDescription = tab.title, ) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
internal fun EmptyDataView(
    modifier: Modifier,
    icon: ImageVector,
    content: String,
) {
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
                imageVector = icon,
                contentDescription = "",
                modifier = modifier.size(140.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = content,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}