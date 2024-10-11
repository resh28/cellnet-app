package com.example.cellnet.feature.dashboard

import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cellnet.core.common.LocationUtil
import com.example.cellnet.core.common.model.CellTowerInfo
import com.example.cellnet.core.common.model.LocationStats
import com.example.cellnet.core.common.model.NetworkInfo
import com.example.cellnet.core.data.iRepository.FirebaseRepository
import com.example.cellnet.core.data.iRepository.LocalStorageRepository
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val localStorageRepository: LocalStorageRepository,
    private val firebaseRepository: FirebaseRepository,
    @ApplicationContext private val context : Context,
): ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        _uiState.update { currentState ->
            currentState.copy(
                isLoadingNetworkInfos = true,
                isLoadingCellTowerInfos = true,
                isLoadingMap = true,
                isLoadingNearestTower = true,
                cellTowerInfos = emptyList(),
                networkInfos = emptyList(),
                avgDownloadSpeed = 0.0,
                avgUploadSpeed = 0.0,
                avgSignalStrength = 0,
                frequentlyConnectedTowerInfo = null
            )
        }
        viewModelScope.launch {
            val networkInfoChannel = Channel<Unit>()

            launch { getNetworkInfos(networkInfoChannel) }
            networkInfoChannel.receive() // Wait for the signal

            getCellTowerInfo()
            getLocationStats()
            getCurrentLocation()
        }
    }

    private suspend fun getCurrentLocation() {
        val location = LocationUtil.getLastKnownLocation(context)
        val currentLocation = Location("")
        location?.let {
            currentLocation.longitude = location.longitude
            currentLocation.latitude = location.latitude
        }
        _uiState.update { currentState ->
            currentState.copy(
                currentLocation = LatLng(currentLocation.latitude, currentLocation.longitude)
            )
        }
    }

    fun updateCurrentPage(value: DashboardTabItem){
        _uiState.update { currentState ->
            currentState.copy(currentPage = value)
        }
    }

    fun updateShowFilterBottomSheet(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(showFilterBottomSheet = value)
        }
    }

    fun updateDurationOfData(value: String) {
        _uiState.update { currentState ->
            currentState.copy(durationOfData = value)
        }
    }

    fun updateDurationOfDataTextFieldValue(value: String) {
        _uiState.update { currentState ->
            currentState.copy(durationOfDataTextFiledValue = value)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getNetworkInfos(channel: Channel<Unit>) = withContext(Dispatchers.Default) {
        val result = firebaseRepository.getLastNetworkInfo(_uiState.value.durationOfData.toLong())
        result.onSuccess {
            _uiState.update { currentState ->
                currentState.copy(
                    networkInfos = it.reversed(),
                    isNetworkInfoFetchError = false,
                    isLoadingNetworkInfos = false
                )
            }
            calculateAvgData()
        }.onFailure { exception ->
            var error = false
            Log.e("GetNetworkInfos", "getNetworkInfos:failure: ${exception.message}")
//            Util.showSnackbar(SnackbarInfoLevel.ERROR, "Unable to fetch data. Please try again later.")
            if (exception.message != "No network data found")
                error = true
            _uiState.update { currentState ->
                currentState.copy(
                    isNetworkInfoFetchError = error,
                    isLoadingNetworkInfos = false
                )
            }
        }
        channel.send(Unit)
    }

    private suspend fun getCellTowerInfo() {
        val cellTowerIds: MutableList<String> = mutableListOf()
        for (data in _uiState.value.networkInfos) {
            if (!cellTowerIds.contains(data.cellTowerId))
                cellTowerIds.add(data.cellTowerId)
        }

        if (cellTowerIds.isNotEmpty()) {
            val result = firebaseRepository.getCellTowerData(cellTowerIds)
            result.onSuccess {
                _uiState.update { currentState ->
                    currentState.copy(
                        cellTowerInfos = it,
                        isCellTowerInfoFetchError = false,
                        isLoadingCellTowerInfos = false
                    )
                }
                getFrequentlyConnectedTower()
                getNearestTower()
            }.onFailure { exception ->
                var error = false
                Log.e("GetCellTowerInfos", "getCellTowerInfo:failure", exception)
                if (exception.message != "No cell tower data found")
                    error = true
                _uiState.update { currentState ->
                    currentState.copy(
                        isCellTowerInfoFetchError = error,
                        isLoadingCellTowerInfos = false
                    )
                }
//            Util.showSnackbar(SnackbarInfoLevel.ERROR, "Unable to fetch data. Please try again later.")
            }
        }
    }

    private fun calculateAvgData() {
        var downloadSpeedSum = 0
        var uploadSpeedSum = 0
        var signalStrengthSum = 0
        var count = 0

        for (data in _uiState.value.networkInfos){
            downloadSpeedSum += data.downloadSpeed
            uploadSpeedSum += data.uploadSpeed
            signalStrengthSum += data.signalStrength ?: 0
            count++
        }

        _uiState.update {currentState ->
            currentState.copy(
                avgDownloadSpeed = (downloadSpeedSum/count).toDouble(),
                avgUploadSpeed = (uploadSpeedSum/count).toDouble(),
                avgSignalStrength = signalStrengthSum/count
            )
        }
    }

    fun getCellTowerData(uId: String): CellTowerInfo? {
        return _uiState.value.cellTowerInfos.firstOrNull { it.uid == uId }
    }

    fun getFrequentNetworkOperator(): String {
        val operatorFrequency = _uiState.value.networkInfos.groupingBy { it.networkOperator }.eachCount()
        val mostFrequentNetworkOperator = operatorFrequency.maxByOrNull { it.value }?.key
        return mostFrequentNetworkOperator ?: ""
    }

    fun getFrequentNetworkClass(): String {
        val classFrequency = _uiState.value.networkInfos.groupingBy { it.networkClass }.eachCount()
        val mostFrequentNetworkClass = classFrequency.maxByOrNull { it.value }?.key
        return mostFrequentNetworkClass ?: ""
    }

    private fun getFrequentlyConnectedTower() {
        val classFrequency = _uiState.value.networkInfos.groupingBy { it.cellTowerId }.eachCount()
        val mostFrequentCellTowerId = classFrequency.maxByOrNull { it.value }?.key
        if (!mostFrequentCellTowerId.isNullOrEmpty())
            _uiState.update { currentState ->
                currentState.copy(
                    frequentlyConnectedTowerInfo = getCellTowerData(mostFrequentCellTowerId),
                    isLoadingMap = false
                )
            }
    }

    private fun getNearestTower() {
        var nearestTowerInfo: CellTowerInfo? = null
        var shortestDistance = Float.MAX_VALUE

        for (tower in _uiState.value.cellTowerInfos) {
            val result = FloatArray(1)
            Location.distanceBetween(
                _uiState.value.currentLocation.latitude, _uiState.value.currentLocation.longitude,
                tower.lat, tower.lng,
                result
            )

            if (result[0] < shortestDistance) {
                shortestDistance = result[0]
                nearestTowerInfo = tower
            }
        }
        if (nearestTowerInfo != null)
            _uiState.update { currentState ->
                currentState.copy(
                    nearestTowerInfo = nearestTowerInfo,
                    isLoadingNearestTower = false
                )
            }
    }

    private fun filterValidData(networkInfoList: List<NetworkInfo>): List<NetworkInfo> {
        return networkInfoList.filter { it.signalStrength != null && it.latitude != 0.0 && it.longitude != 0.0 }
    }

    private fun groupByLocation(networkInfoList: List<NetworkInfo>, threshold: Double = 0.01): Map<Pair<Double, Double>, List<NetworkInfo>> {
        return networkInfoList.groupBy { info ->
            Pair(
                (info.latitude / threshold).roundToInt() * threshold,
                (info.longitude / threshold).roundToInt() * threshold
            )
        }
    }

    private fun calculateLocationStats(groupedData: Map<Pair<Double, Double>, List<NetworkInfo>>): List<LocationStats> {
        return groupedData.map { (location, infoList) ->
            val avgSignalStrength = infoList.mapNotNull { it.signalStrength }.average()
            val avgDownloadSpeed = infoList.map { it.downloadSpeed }.average().toInt()
            val avgUploadSpeed = infoList.map { it.uploadSpeed }.average().toInt()

            LocationStats(
                latitude = location.first,
                longitude = location.second,
                averageSignalStrength = avgSignalStrength,
                averageDownloadSpeed = avgDownloadSpeed,
                averageUploadSpeed = avgUploadSpeed
            )
        }
    }

    private fun calculateLocationScore(signalStrength: Double, downloadSpeed: Int, uploadSpeed: Int): Double {
        val signalWeight = 0.7
        val speedWeight = 0.3
        return (signalStrength * -1 * signalWeight) + ((downloadSpeed + uploadSpeed) / 2.0 * speedWeight)
    }

    private fun calculateScores(locationStats: List<LocationStats>): List<Pair<LocationStats, Double>> {
        return locationStats.map { stats ->
            val score = calculateLocationScore(stats.averageSignalStrength, stats.averageDownloadSpeed, stats.averageUploadSpeed)
            Pair(stats, score)
        }.sortedByDescending { it.second } // Sort by highest score
    }

    private fun getBestLocations(locationScores: List<Pair<LocationStats, Double>>, scoreThreshold: Double): List<LocationStats> {
        return locationScores.filter { it.second >= scoreThreshold }.map { it.first }
    }

    private fun getLocationStats() {
        val validData = filterValidData(_uiState.value.networkInfos)
        val groupedData = groupByLocation(validData)
        val locationStats = calculateLocationStats(groupedData)
        val locationScores = calculateScores(locationStats)
        val bestLocations = getBestLocations(locationScores, scoreThreshold = 70.0)

        _uiState.update { currentState ->
            currentState.copy(
                locationScores = locationScores
            )
        }
    }

    fun getHueFromColor(color: Color): Float {
        return when (color) {
            Color.Green -> BitmapDescriptorFactory.HUE_GREEN
            Color.Yellow -> BitmapDescriptorFactory.HUE_YELLOW
            else -> BitmapDescriptorFactory.HUE_RED
        }
    }
}