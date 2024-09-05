package com.example.cellnet.core.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.cellnet.core.common.model.DeviceInfo
import com.example.cellnet.core.common.model.SnackbarInfoLevel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


object Util {
    private var job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val snackBarNotificationFlow = MutableStateFlow(Pair(SnackbarInfoLevel.INFO, ""))


    @SuppressLint("HardwareIds")
    fun getDeviceInfo(context: Context): DeviceInfo {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val productName = Build.PRODUCT
        val model = Build.MODEL
        val manufacturer = Build.MANUFACTURER
        val osVersion = Build.VERSION.RELEASE
        val appVersion = getPackageInfo(context).versionName
        val phoneType = NetworkUtil.getPhoneType(context)
        return DeviceInfo(
            androidId, productName, model, manufacturer, osVersion, appVersion, "", phoneType
        )

    }

    private fun getPackageInfo(context: Context, flags: Int = 0): PackageInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(flags.toLong())
            )
        } else {
            context.packageManager.getPackageInfo(
                context.packageName,
                flags
            )
        }


    /**
     * call this method for receive location
     * get location and give callback when successfully retrieve
     * function itself check location permission before access related methods
     *
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getLastKnownLocation(context: Context): Location? {
        return suspendCancellableCoroutine { continuation ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            Log.d("location", "${location.latitude} ${location.longitude}")
                            continuation.resume(location)
                        } else {
                            continuation.resume(null)
                        }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } else {
                continuation.resume(null)
            }
        }
    }

    fun showSnackbar(infoLevel: SnackbarInfoLevel, message: String) {
        scope.launch {
            snackBarNotificationFlow.tryEmit(Pair(infoLevel, message))
            delay(5000)
            snackBarNotificationFlow.tryEmit(Pair(SnackbarInfoLevel.INFO, ""))
        }
    }

    fun showSnackbarIndefinite(infoLevel: SnackbarInfoLevel, message: String) {
        scope.launch {
            snackBarNotificationFlow.tryEmit(Pair(infoLevel, message))
        }
    }

    fun getSnackbarFlow(): MutableStateFlow<Pair<SnackbarInfoLevel, String>> {
        return snackBarNotificationFlow
    }

}