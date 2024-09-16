package com.example.cellnet.feature.dashboard

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cellnet.core.common.model.CellTowerInfo
import com.example.cellnet.core.data.iRepository.FirebaseRepository
import com.example.cellnet.core.data.iRepository.LocalStorageRepository
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
                isLoadingCellTowerInfos = true
            )
        }
        viewModelScope.launch {
            val networkInfoChannel = Channel<Unit>()

            launch { getNetworkInfos(networkInfoChannel) }
            networkInfoChannel.receive() // Wait for the signal

            getCellTowerInfo()
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
            Log.e("GetNetworkInfos", "getNetworkInfos:failure", exception)
//            Util.showSnackbar(SnackbarInfoLevel.ERROR, "Unable to fetch data. Please try again later.")
            _uiState.update { currentState ->
                currentState.copy(
                    isNetworkInfoFetchError = true,
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

        val result = firebaseRepository.getCellTowerData(cellTowerIds)
        result.onSuccess {
            _uiState.update { currentState ->
                currentState.copy(
                    cellTowerInfos = it,
                    isCellTowerInfoFetchError = true,
                    isLoadingCellTowerInfos = false
                )
            }
            calculateAvgData()
        }.onFailure { exception ->
            Log.e("GetCellTowerInfos", "getCellTowerInfo:failure", exception)
            _uiState.update { currentState ->
                currentState.copy(
                    isCellTowerInfoFetchError = true,
                    isLoadingCellTowerInfos = false
                )
            }
//            Util.showSnackbar(SnackbarInfoLevel.ERROR, "Unable to fetch data. Please try again later.")
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
}