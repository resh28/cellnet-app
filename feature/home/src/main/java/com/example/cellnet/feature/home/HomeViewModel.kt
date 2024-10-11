package com.example.cellnet.feature.home

import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cellnet.core.common.LocationUtil
import com.example.cellnet.core.common.NetworkUtil
import com.example.cellnet.core.common.Util
import com.example.cellnet.core.common.model.CellTowerInfo
import com.example.cellnet.core.common.model.NetworkInfo
import com.example.cellnet.core.common.model.SnackbarInfoLevel
import com.example.cellnet.core.data.iRepository.FirebaseRepository
import com.example.cellnet.core.data.iRepository.LocalStorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val localStorageRepository: LocalStorageRepository,
    private val firebaseRepository: FirebaseRepository,
    @ApplicationContext private val context : Context,
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userChannel = Channel<Unit>()

            launch { collectUserData(userChannel) }
            userChannel.receive() // Wait for the signal

            launch { getDeviceInfo() }

        }
    }

    fun updateIsScanning(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isScanning = value
            )
        }
    }

    fun updateIsDataUploading(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isDataUploading = value
            )
        }
    }

    fun updateIsDataUploaded(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isDataUploaded = value
            )
        }
    }

    private suspend fun collectUserData(channel: Channel<Unit>) = withContext(Dispatchers.Default) {
        localStorageRepository.getUser().collect {user ->
            _uiState.update { currentState ->
                currentState.copy(
                    userData = user
                )
            }
            channel.send(Unit)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDeviceInfo() {
        val deviceInfo = Util.getDeviceInfo(context)
        deviceInfo.userId = _uiState.value.userData.userId
        _uiState.update { currentState ->
            currentState.copy(
                deviceInfo = deviceInfo
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun onScan(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val cellTowerInfo = getCellTowerInfo(context)

            val location = LocationUtil.getLastKnownLocation(context)
            val currentLocation = Location("")
            location?.let {
                currentLocation.longitude = location.longitude
                currentLocation.latitude = location.latitude
            }

            val downSpeed = NetworkUtil.getNetworkDownSpeed(context)
            val upSpeed = NetworkUtil.getNetworkUpSpeed(context)
            val networkClass = NetworkUtil.getNetworkClass(context)
            val signalStrength = NetworkUtil.getSignalStrength(context)
            val networkOperator = NetworkUtil.getNetworkOperator(context)
            val phoneType = NetworkUtil.getPhoneType(context)
            val wifiSSID = NetworkUtil.getWifiSSID(context)
            val currentDateTime = Date()

            _uiState.update { currentState ->
                currentState.copy(
                    cellTowerInfo = cellTowerInfo,
                    networkDownSpeed = downSpeed,
                    networkUpSpeed = upSpeed,
                    networkClass = networkClass,
                    signalStrength = signalStrength,
                    networkOperator = networkOperator,
                    phoneType = phoneType,
                    wifiSSID = wifiSSID ?: "",
                    dateTime = currentDateTime,
                    currentLocation = currentLocation,
                    isScanData = true,
                    isScanning = false,
                    isDataUploaded = false
                )
            }
            if (networkClass.isNotEmpty() && signalStrength != null && networkOperator.isNotEmpty() && location != null
                && cellTowerInfo.mcc.isNotEmpty() && cellTowerInfo.mnc.isNotEmpty() && cellTowerInfo.cid != null && cellTowerInfo.lac != null)
                uploadData()
            else Util.showSnackbar(SnackbarInfoLevel.ERROR, "Unable to upload data. Please rescan and try again.")
        }
    }

    fun getCurrentLocation(context: Context) {
        viewModelScope.launch {
            try {
                val location = LocationUtil.getLastKnownLocation(context)
                val currentLocation = Location("")

                location?.let {
                    currentLocation.longitude = location.longitude
                    currentLocation.latitude = location.latitude
                    _uiState.update { currentState ->
                        currentState.copy(
                            currentLocation = currentLocation
                        )
                    }
                }
            } catch (e: Exception) {
                e.message?.let { Log.e("getLocation", it) }
            }

        }
    }

    private fun getCellTowerInfo(context: Context): CellTowerInfo {
        var cellTowerLocation = Location("")
        // Perform network operation here
        val cellTowerData = NetworkUtil.getCellTowerData(context)
        val networkOperatorCodes = NetworkUtil.getNetworkOperatorCodes(context)
        if (cellTowerData.cid != null && cellTowerData.lac != null) {
            cellTowerLocation = LocationUtil.fetchCellTowerLocationDetails(cellTowerData, context, networkOperatorCodes)
        }
        return CellTowerInfo(
            uid = "${networkOperatorCodes.mcc}-${networkOperatorCodes.mnc}-${cellTowerData.lac}-${cellTowerData.cid}",
            cid = cellTowerData.cid,
            lac = cellTowerData.lac,
            mcc = networkOperatorCodes.mcc,
            mnc = networkOperatorCodes.mnc,
            lat = cellTowerLocation.latitude,
            lng = cellTowerLocation.longitude
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadData() {
        viewModelScope.launch {
            val savedCellTowerData =  saveCellTowerInfo()
            val savedNetworkData =  saveNetworkInfo()

            if (savedNetworkData && savedCellTowerData) {
                updateIsDataUploading(false)
                updateIsDataUploaded(true)
                Util.showSnackbar(SnackbarInfoLevel.SUCCESS, "Data uploaded successfully")
            } else {
                updateIsDataUploading(false)
                Util.showSnackbar(SnackbarInfoLevel.ERROR, "Data uploading failed. Please try again.")
            }

        }
    }

    private suspend fun saveCellTowerInfo(): Boolean {
        var successful = false
        val result = firebaseRepository.saveCellTowerInfo(_uiState.value.cellTowerInfo)
        result.onSuccess {
            Log.d("save:CellTowerInfo", it)
            successful = true
        }.onFailure { exception ->
            Log.w("save:CellTowerInfo", "saveCellTowerInfo:failure", exception)
            successful = false
        }
        return successful
    }

    private suspend fun saveNetworkInfo(): Boolean {
        var successful = false
        val networkInfo = NetworkInfo(
            userId = _uiState.value.userData.userId,
            cellTowerId = _uiState.value.cellTowerInfo.uid,
            deviceId = _uiState.value.deviceInfo.androidId,
            networkOperator = _uiState.value.networkOperator,
            networkClass = _uiState.value.networkClass,
            phoneType = _uiState.value.phoneType,
            downloadSpeed = _uiState.value.networkDownSpeed,
            uploadSpeed = _uiState.value.networkUpSpeed,
            signalStrength = _uiState.value.signalStrength,
            timeStamp = _uiState.value.dateTime,
            latitude = _uiState.value.currentLocation.latitude,
            longitude = _uiState.value.currentLocation.longitude
        )
        val result = firebaseRepository.saveNetworkInfo(networkInfo)
        result.onSuccess {
            Log.d("save:NetworkInfo", it)
            successful = true
        }.onFailure { exception ->
            Log.w("save:NetworkInfo", "saveNetworkInfo:failure", exception)
            successful = false
        }
        return successful
    }

}