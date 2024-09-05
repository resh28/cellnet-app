package com.example.cellnet.core.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.example.cellnet.core.common.model.DeviceInfo
import com.example.cellnet.core.common.model.SnackbarInfoLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


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